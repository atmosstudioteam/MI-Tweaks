# MI Tweaks Forked

A performance-focused fork of [MI Tweaks](https://github.com/Swedz/MI-Tweaks), an addon for [Modern Industrialization](https://github.com/AztechMC/Modern-Industrialization) that provides configurable tweaks aimed at modpack creators.

This fork keeps the original `mi_tweaks` mod ID so it can be used as a drop-in replacement. Do not install it together with the original MI Tweaks JAR.

## Performance changes

The 1.21.1 fork fixes severe Machine Blueprint GUI slowdowns caused by repeatedly resolving `machine_blueprints.machines`. Resolving that config value reconstructs `MachineList`, including regex matching against the Minecraft block registry, so doing it from tooltip and EMI render paths can become extremely expensive in large modpacks.

The resolved machine list is cached and shared by blueprint validation, tooltips, creative-tab variants, placement checks, and the EMI blueprint-copy recipe.

The original Machine Blueprint controller-block overlay is retained. The renderer reads the stored machine block directly from the blueprint item component, so rendering the overlay does not resolve or rebuild the machine-list config.

## Documentation

The original feature documentation is retained in [`docs/`](docs/).

## Credits and license

MI Tweaks was originally created by [Swedz](https://github.com/Swedz). This fork is maintained by Atmos Studio and is distributed under the original MIT License. See [`LICENSE`](LICENSE).
