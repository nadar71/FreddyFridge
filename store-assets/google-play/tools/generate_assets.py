#!/usr/bin/env python3
"""Build reproducible Google Play graphics from real FreddyFridge captures."""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont


ROOT = Path(__file__).resolve().parents[3]
STORE = ROOT / "store-assets" / "google-play"
RES = ROOT / "app" / "src" / "main" / "res"

GREEN = "#7AA44D"
GREEN_DARK = "#466D29"
GREEN_LIGHT = "#A9C885"
CREAM = "#FFF9ED"
ORANGE = "#EC9039"
BROWN = "#734B26"

FONT_BOLD = RES / "font" / "fredoka_bold.ttf"
FONT_SEMIBOLD = RES / "font" / "fredoka_semibold.ttf"
FONT_REGULAR = RES / "font" / "fredoka_regular.ttf"

CAPTIONS = {
    "en-US": [
        ("01-home.png", "Keep food fresh, waste less"),
        ("02-expiring.png", "See what expires next"),
        ("03-consumed.png", "Remember what you consumed"),
        ("04-expired.png", "Learn from expired items"),
        ("05-add.png", "Add products in seconds"),
        ("06-settings.png", "Reminders that fit your routine"),
        ("07-credits.png", "Friendly, focused and transparent"),
    ],
    "it-IT": [
        ("01-home.png", "Cibo fresco, meno sprechi"),
        ("02-expiring.png", "Controlla le prossime scadenze"),
        ("03-consumed.png", "Tieni traccia di ciò che consumi"),
        ("04-expired.png", "Monitora i prodotti scaduti"),
        ("05-add.png", "Aggiungi prodotti in pochi secondi"),
        ("06-settings.png", "Promemoria adatti alle tue abitudini"),
        ("07-credits.png", "Semplice, trasparente e amichevole"),
    ],
}


def fit_text(draw, text, font_path, max_size, min_size, max_width):
    for size in range(max_size, min_size - 1, -1):
        font = ImageFont.truetype(font_path, size)
        if draw.textbbox((0, 0), text, font=font)[2] <= max_width:
            return font
    return ImageFont.truetype(font_path, min_size)


def rounded_image(image, radius):
    mask = Image.new("L", image.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, *image.size), radius=radius, fill=255)
    result = image.convert("RGBA")
    result.putalpha(mask)
    return result


def draw_background(size):
    width, height = size
    canvas = Image.new("RGB", size, GREEN)
    draw = ImageDraw.Draw(canvas)
    draw.ellipse((-240, -260, 680, 510), fill=GREEN_LIGHT)
    draw.ellipse((660, height - 430, 1260, height + 150), fill=GREEN_DARK)
    return canvas


def create_screenshot(locale, index, filename, caption):
    canvas = draw_background((1080, 1920))
    draw = ImageDraw.Draw(canvas)
    eyebrow = "FREDDYFRIDGE"
    eyebrow_font = ImageFont.truetype(FONT_SEMIBOLD, 28)
    draw.text((540, 58), eyebrow, font=eyebrow_font, fill=CREAM, anchor="ma")

    caption_font = fit_text(draw, caption, FONT_BOLD, 62, 43, 920)
    draw.text((540, 122), caption, font=caption_font, fill=CREAM, anchor="ma")

    source = Image.open(STORE / "source" / "screenshots" / locale / filename).convert("RGB")
    source = source.crop((0, 63, source.width, source.height - 63))
    target_width = 760
    target_height = round(source.height * target_width / source.width)
    source = source.resize((target_width, target_height), Image.Resampling.LANCZOS)
    source = rounded_image(source, 44)

    left = (1080 - target_width) // 2
    top = 282
    ambient_shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    ambient_draw = ImageDraw.Draw(ambient_shadow)
    ambient_draw.rounded_rectangle(
        (left - 24, top - 10, left + target_width + 24, top + target_height + 42),
        radius=70,
        fill=(37, 57, 27, 100),
    )
    ambient_shadow = ambient_shadow.filter(ImageFilter.GaussianBlur(32))

    contact_shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    contact_draw = ImageDraw.Draw(contact_shadow)
    contact_draw.rounded_rectangle(
        (left - 5, top + 12, left + target_width + 5, top + target_height + 24),
        radius=52,
        fill=(34, 48, 25, 85),
    )
    contact_shadow = contact_shadow.filter(ImageFilter.GaussianBlur(12))

    canvas = Image.alpha_composite(canvas.convert("RGBA"), ambient_shadow)
    canvas = Image.alpha_composite(canvas, contact_shadow)
    canvas.alpha_composite(source, (left, top))

    output = STORE / locale / "phone-screenshots" / f"{index:02d}-{filename[3:]}"
    canvas.convert("RGB").save(output, "PNG", optimize=True)


def create_feature_graphic(locale):
    canvas = Image.new("RGB", (1024, 500), GREEN_DARK)
    draw = ImageDraw.Draw(canvas)
    draw.ellipse((-180, -240, 550, 470), fill=GREEN)
    draw.ellipse((710, -100, 1160, 350), fill=ORANGE)
    draw.rounded_rectangle((62, 65, 582, 435), radius=60, fill=CREAM)

    if locale == "en-US":
        headline = "Less waste.\nMore taste."
        subtitle = "Know what expires next."
    else:
        headline = "Meno sprechi.\nPiù gusto."
        subtitle = "Sai sempre cosa scade prima."

    headline_font = ImageFont.truetype(FONT_BOLD, 63)
    subtitle_font = fit_text(draw, subtitle, FONT_REGULAR, 29, 23, 450)
    draw.multiline_text((108, 112), headline, font=headline_font, fill=BROWN, spacing=0)
    draw.text((108, 350), subtitle, font=subtitle_font, fill=GREEN_DARK)

    fridge = Image.open(RES / "drawable" / "fridge_foreground.png").convert("RGBA")
    fridge.thumbnail((410, 410), Image.Resampling.LANCZOS)
    shadow = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    fridge_left = 620 + (360 - fridge.width) // 2
    fridge_top = 55 + (390 - fridge.height) // 2
    shadow_draw = ImageDraw.Draw(shadow)
    shadow_draw.ellipse((650, 390, 985, 462), fill=(35, 58, 24, 105))
    shadow = shadow.filter(ImageFilter.GaussianBlur(14))
    canvas = Image.alpha_composite(canvas.convert("RGBA"), shadow)
    canvas.alpha_composite(fridge, (fridge_left, fridge_top))
    canvas.convert("RGB").save(STORE / locale / "feature-graphic-1024x500.png", "PNG", optimize=True)


def main():
    for locale, items in CAPTIONS.items():
        for index, (filename, caption) in enumerate(items, start=1):
            create_screenshot(locale, index, filename, caption)
        create_feature_graphic(locale)


if __name__ == "__main__":
    main()
