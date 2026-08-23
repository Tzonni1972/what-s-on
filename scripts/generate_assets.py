#!/usr/bin/env python3
"""Generate cream paper texture, TV banner, and launcher icons."""

from __future__ import annotations

import math
import os
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app" / "src" / "main" / "res"
FONT_REG = RES / "font" / "playfair_display_regular.ttf"
FONT_BOLD = RES / "font" / "playfair_display_bold.ttf"
FONT_ITAL = RES / "font" / "playfair_display_italic.ttf"

PAPER = (247, 241, 230, 255)
CHARCOAL = (28, 24, 20, 255)
GOLD = (158, 112, 42, 255)
RED = (178, 58, 44, 255)


def paper(size: tuple[int, int], seed: int = 24) -> Image.Image:
    rng = random.Random(seed)
    img = Image.new("RGBA", size, PAPER)
    px = img.load()
    w, h = size
    for _ in range(int(w * h * 0.08)):
        x = rng.randint(0, w - 1)
        y = rng.randint(0, h - 1)
        d = rng.randint(-14, 10)
        r = max(0, min(255, PAPER[0] + d))
        g = max(0, min(255, PAPER[1] + d - 1))
        b = max(0, min(255, PAPER[2] + d - 3))
        px[x, y] = (r, g, b, 255)
    return img.filter(ImageFilter.GaussianBlur(0.4))


def draw_wordmark(draw: ImageDraw.ImageDraw, x: int, y: int, scale: float) -> None:
    bold = ImageFont.truetype(str(FONT_BOLD), int(42 * scale))
    ital = ImageFont.truetype(str(FONT_ITAL), int(32 * scale))
    draw.text((x, y), "What's ", font=bold, fill=CHARCOAL)
    whats_w = draw.textlength("What's ", font=bold)
    draw.text((x + whats_w, y + int(8 * scale)), "on", font=ital, fill=GOLD)


def draw_rule(draw: ImageDraw.ImageDraw, x0: int, x1: int, y: int) -> None:
    for x in range(x0, x1):
        t = (x - x0) / max(1, x1 - x0)
        r = int(GOLD[0] + (RED[0] - GOLD[0]) * t)
        g = int(GOLD[1] + (RED[1] - GOLD[1]) * t)
        b = int(GOLD[2] + (RED[2] - GOLD[2]) * t)
        draw.point((x, y), fill=(r, g, b, 255))
        draw.point((x, y + 1), fill=(r, g, b, 220))


def write_png(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    img.save(path, "PNG")
    print("wrote", path, img.size)


def main() -> None:
    texture = paper((1920, 1080))
    write_png(texture, RES / "drawable" / "paper_texture.png")

    banner = paper((320, 180), seed=7)
    draw = ImageDraw.Draw(banner)
    draw_wordmark(draw, 18, 58, 0.72)
    draw_rule(draw, 20, 300, 122)
    small = ImageFont.truetype(str(FONT_REG), 14)
    draw.text((20, 136), "ATHENS", font=small, fill=CHARCOAL)
    write_png(banner, RES / "drawable" / "tv_banner.png")

    densities = {
        "mdpi": 48,
        "hdpi": 72,
        "xhdpi": 96,
        "xxhdpi": 144,
        "xxxhdpi": 192,
    }
    for name, size in densities.items():
        icon = paper((size, size), seed=3)
        d = ImageDraw.Draw(icon)
        scale = size / 96
        font = ImageFont.truetype(str(FONT_BOLD), max(18, int(46 * scale)))
        ital = ImageFont.truetype(str(FONT_ITAL), max(12, int(28 * scale)))
        d.text((size * 0.14, size * 0.18), "W", font=font, fill=CHARCOAL)
        d.text((size * 0.52, size * 0.42), "on", font=ital, fill=GOLD)
        write_png(icon, RES / f"mipmap-{name}" / "ic_launcher.png")


if __name__ == "__main__":
    main()
