# FreddyFridge Google Play assets

This directory contains the production-ready Google Play listing pack for `en-US` and `it-IT`.

## Upload map

- `shared/icon-512.png`: high-resolution app icon, 512 x 512 PNG.
- `<locale>/feature-graphic-1024x500.png`: localized feature graphic.
- `<locale>/phone-screenshots/*.png`: seven localized phone screenshots, 1080 x 1920.
- `<locale>/listing.txt`: app name, short description, and full description.
- `<locale>/release-notes.txt`: localized release notes.
- `<locale>/screenshot-alt-text.txt`: accessibility descriptions in upload order.

## Screenshot order

1. Home and today's expiry
2. Upcoming expiries
3. Consumed products
4. Expired products
5. Add product flow
6. Reminder settings
7. Credits and attributions

All screenshots use real application captures with deterministic demo data. Marketing captions occupy less than 20% of each image. No device frame, ranking claim, pricing language, testimonial, or call to action is included.

## Regeneration

Run with the bundled Codex Python runtime or any Python installation containing Pillow:

```bash
/Users/simone/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/bin/python3 store-assets/google-play/tools/generate_assets.py
```

The source screenshots are retained under `source/screenshots`. The app icon remains the project's existing Play Store icon, and the feature graphic reuses the original fridge artwork.
