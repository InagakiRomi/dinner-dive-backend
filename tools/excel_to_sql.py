from __future__ import annotations

from datetime import datetime
from pathlib import Path
import sys
from typing import Any

import pandas as pd

# 不產生 .pyc / __pycache__
sys.dont_write_bytecode = True

BASE_DIR = Path(__file__).resolve().parent
DATA_DIR = (BASE_DIR / "../data").resolve()
OUTPUT_DIR = (BASE_DIR / "../my-dinnerdive/src/main/resources").resolve()
OUTPUT_FILE = "data.sql"
# 依資料相依性控制輸出順序，避免外鍵初始化失敗
TABLE_ORDER = ["user_groups", "restaurants", "restaurant_history", "dishes", "users"]


def escape_sql(value: Any) -> str:
    if pd.isna(value):
        return "NULL"
    if isinstance(value, str):
        return "'" + value.replace("'", "''") + "'"
    if isinstance(value, (datetime, pd.Timestamp)):
        return "'" + value.strftime("%Y-%m-%d %H:%M:%S") + "'"
    if isinstance(value, bool):
        return "TRUE" if value else "FALSE"
    return str(value)


def dataframe_to_insert_sql(df: pd.DataFrame, table_name: str) -> str:
    columns = df.columns.tolist()
    insert_sql = f"INSERT INTO {table_name}\n({', '.join(columns)}) VALUES\n"
    values_list = []

    for _, row in df.iterrows():
        values = [escape_sql(row[col]) for col in columns]
        values_list.append(f"({', '.join(values)})")

    return insert_sql + ",\n".join(values_list) + ";"


def load_table_sql_from_excels(data_dir: Path) -> dict[str, str]:
    table_sql: dict[str, str] = {}

    for excel_path in sorted(data_dir.glob("*.xlsx")):
        print(f"讀取檔案：{excel_path}")
        # 從第 2 行開始讀（跳過 index 0 的那一行）
        df = pd.read_excel(excel_path, header=1)
        table_name = excel_path.stem
        table_sql[table_name] = dataframe_to_insert_sql(df, table_name)

    return table_sql


def order_sql_statements(table_sql: dict[str, str]) -> list[str]:
    ordered_statements: list[str] = []
    remaining = dict(table_sql)

    for table_name in TABLE_ORDER:
        if table_name in remaining:
            ordered_statements.append(remaining.pop(table_name))

    for table_name in sorted(remaining.keys()):
        ordered_statements.append(remaining[table_name])

    return ordered_statements


def write_sql_file(statements: list[str], output_dir: Path, output_file: str) -> Path:
    output_dir.mkdir(parents=True, exist_ok=True)
    output_path = output_dir / output_file

    with output_path.open("w", encoding="utf-8") as file:
        file.write("\n\n".join(statements))
        file.write("\n")

    return output_path


def wait_for_any_key() -> None:
    try:
        import msvcrt  # Windows 專用

        print("按下任意鍵關閉視窗...")
        msvcrt.getch()
    except ImportError:
        input("按 Enter 關閉視窗...")


def main() -> None:
    table_sql = load_table_sql_from_excels(DATA_DIR)
    statements = order_sql_statements(table_sql)
    output_path = write_sql_file(statements, OUTPUT_DIR, OUTPUT_FILE)
    print(f"SQL 已匯出至 {output_path}")


if __name__ == "__main__":
    try:
        main()
    finally:
        wait_for_any_key()
