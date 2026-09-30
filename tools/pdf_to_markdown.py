#!/usr/bin/env python3
"""Convert a PDF to Markdown with PyMuPDF4LLM."""

import argparse
import sys
from pathlib import Path


def select_pdf_file() -> Path | None:
    try:
        import tkinter as tk
        from tkinter import filedialog
    except ImportError as error:
        raise RuntimeError(
            "Tkinter is unavailable; pass the PDF path as an argument instead."
        ) from error

    try:
        root = tk.Tk()
        root.withdraw()
        try:
            selected_path = filedialog.askopenfilename(
                title="Select a PDF to convert",
                filetypes=[("PDF files", "*.pdf"), ("All files", "*.*")],
            )
        finally:
            root.destroy()
    except tk.TclError as error:
        raise RuntimeError(
            "The file picker could not open; pass the PDF path as an argument instead."
        ) from error

    return Path(selected_path) if selected_path else None


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "pdf",
        nargs="?",
        type=Path,
        help="PDF to convert; opens a file picker when omitted",
    )
    parser.add_argument(
        "-o",
        "--output",
        type=Path,
        help="Markdown output path (defaults to the PDF path with a .md suffix)",
    )
    return parser.parse_args()


def main() -> int:
    arguments = parse_arguments()

    try:
        pdf_path = arguments.pdf or select_pdf_file()
    except RuntimeError as error:
        print(error, file=sys.stderr)
        return 1

    if pdf_path is None:
        print("No PDF selected.")
        return 0

    pdf_path = pdf_path.expanduser().resolve()
    if not pdf_path.is_file() or pdf_path.suffix.lower() != ".pdf":
        print(f"Not a PDF file: {pdf_path}", file=sys.stderr)
        return 1

    output_path = arguments.output or pdf_path.with_suffix(".md")
    output_path = output_path.expanduser().resolve()
    if output_path == pdf_path:
        print("The Markdown output path must not be the input PDF.", file=sys.stderr)
        return 1

    try:
        import pymupdf4llm
    except ImportError:
        print(
            "PyMuPDF4LLM is not installed. Install it with:\n"
            "  python3 -m pip install -r tools/requirements.txt",
            file=sys.stderr,
        )
        return 1

    try:
        markdown = pymupdf4llm.to_markdown(str(pdf_path))
        output_path.write_text(markdown, encoding="utf-8")
    except Exception as error:
        print(f"Could not convert {pdf_path}: {error}", file=sys.stderr)
        return 1

    print(f"Markdown written to {output_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())