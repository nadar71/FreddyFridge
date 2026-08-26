#!/usr/bin/env python3
"""Generate Android launcher resources from the approved Play Store artwork."""

from collections import deque
from pathlib import Path

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[3]
SOURCE = ROOT / "store-assets" / "google-play" / "source" / "icon-artwork.png"
PLAY_ICON = ROOT / "store-assets" / "google-play" / "shared" / "icon-512.png"
PROJECT_PLAY_ICON = ROOT / "app" / "src" / "main" / "ic_launcher-playstore.png"
RES = ROOT / "app" / "src" / "main" / "res"

GREEN = (122, 164, 77, 255)
DENSITIES = {
    "mdpi": (48, 108),
    "hdpi": (72, 162),
    "xhdpi": (96, 216),
    "xxhdpi": (144, 324),
    "xxxhdpi": (192, 432),
}


def remove_connected_white_background(image):
    image = image.convert("RGBA")
    pixels = image.load()
    width, height = image.size
    visited = bytearray(width * height)
    queue = deque()

    for x in range(width):
        queue.append((x, 0))
        queue.append((x, height - 1))
    for y in range(height):
        queue.append((0, y))
        queue.append((width - 1, y))

    while queue:
        x, y = queue.popleft()
        index = y * width + x
        if visited[index]:
            continue
        visited[index] = 1
        red, green, blue, _ = pixels[x, y]
        if min(red, green, blue) < 242:
            continue
        pixels[x, y] = (red, green, blue, 0)
        if x > 0:
            queue.append((x - 1, y))
        if x + 1 < width:
            queue.append((x + 1, y))
        if y > 0:
            queue.append((x, y - 1))
        if y + 1 < height:
            queue.append((x, y + 1))

    box = image.getbbox()
    return image.crop(box) if box else image


def badge_icon(size, artwork, circular_canvas=False):
    image = Image.new("RGBA", (size, size), (0, 0, 0, 0) if circular_canvas else GREEN)
    draw = ImageDraw.Draw(image)
    if circular_canvas:
        draw.ellipse((0, 0, size - 1, size - 1), fill=GREEN)

    art = artwork.copy()
    art.thumbnail((round(size * 0.80), round(size * 0.80)), Image.Resampling.LANCZOS)
    image.alpha_composite(art, ((size - art.width) // 2, (size - art.height) // 2))
    return image


def adaptive_foreground(size, artwork):
    image = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    art = artwork.copy()
    art.thumbnail((round(size * 0.60), round(size * 0.60)), Image.Resampling.LANCZOS)
    image.alpha_composite(art, ((size - art.width) // 2, (size - art.height) // 2))
    return image


def monochrome_foreground(size, artwork):
    art = artwork.copy()
    art.thumbnail((round(size * 0.53), round(size * 0.53)), Image.Resampling.LANCZOS)
    result = Image.new("RGBA", (size, size), (255, 255, 255, 0))
    silhouette = Image.new("RGBA", art.size, (255, 255, 255, 0))
    silhouette.putalpha(art.getchannel("A"))
    result.alpha_composite(silhouette, ((size - art.width) // 2, (size - art.height) // 2))
    return result


def main():
    artwork = remove_connected_white_background(Image.open(SOURCE))
    play_icon = badge_icon(512, artwork)
    play_icon.save(PLAY_ICON, "PNG", optimize=True)
    play_icon.save(PROJECT_PLAY_ICON, "PNG", optimize=True)

    for density, (legacy_size, adaptive_size) in DENSITIES.items():
        destination = RES / f"mipmap-{density}"
        badge_icon(legacy_size, artwork).save(
            destination / "ic_launcher.webp", "WEBP", lossless=True, method=6
        )
        badge_icon(legacy_size, artwork, circular_canvas=True).save(
            destination / "ic_launcher_round.webp", "WEBP", lossless=True, method=6
        )
        adaptive_foreground(adaptive_size, artwork).save(
            destination / "ic_launcher_foreground.webp", "WEBP", lossless=True, method=6
        )
        monochrome_foreground(adaptive_size, artwork).save(
            destination / "ic_launcher_monochrome.webp", "WEBP", lossless=True, method=6
        )


if __name__ == "__main__":
    main()
