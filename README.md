<div align="center">
<img src=".github/logo.png" alt="Original logo by jnix, edited by djefrey" width="250">
<h1>Colorwheel for Create Fly</h1>
</div>
<br>

### About

The goal of this project is to provide a [Flywheel](https://github.com/Engine-Room/Flywheel) backend that is compatible with [Iris](https://github.com/IrisShaders/Iris) 1.x.  
It does so by providing new shader programs to be provided by shaderpacks. Documentation is available here: https://djefrey.github.io/colorwheel/.

This project started as an attempt to port [Iris Flywheel Compat](https://github.com/leon-o/iris-flw-compat/) from Flywheel 0.6 to 1.0.  
However, with the amount of changes made to Flywheel, I decided to start from scratch.

Aperture, the new shader standard for Iris 2.0, is expected to support Flywheel.  
As such, this mod will no longer be required.  

### Difference with Iris Flywheel Compat

As of writing, Iris Flywheel Compat 1.x is able to adapt the shader code from the shaderpack to work with Flywheel 0.6.  
This was possible because the internal shaders used by Flywheel were very basic.

However, this changed drastically since Flywheel 1.0. The new release introduced many internal changes, including a new material system.  
Internal shaders are now much more complex (from 10 lines to over 1000 lines) and are generated on the fly, as mods such as Create use custom materials with custom shader code.

Colorwheel, instead, implements an extension to the Iris shader standard. This means shaderpacks must include specific programs that will be used by Colorwheel.

### How to use

To use this mod, you need to install Iris and a mod that includes Flywheel (like Create or Vanillin).  
You also need to use a compatible shaderpack.  

You can find Colorwheel releases in the [Releases](https://github.com/djefrey/Colorwheel/releases) section.  
The [Colorwheel Patcher](https://github.com/djefrey/Colorwheel-Patcher) mod can be used to apply automatically patches for shaderpack I support myself. This is not required for shaderpacks supporting Colorwheel themselves.

### Credits

- **djefrey** : Author of the original Colorwheel, which this fork is based on
- **Jozufozu & PepperCode1** : Author and maintainers of Flywheel and Vanillin
- **leon-o** : Author of Iris Flywheel Compat
- **IMS** : Lead developer of Iris
- **douira** : Author of [glsl-transformer](https://github.com/IrisShaders/glsl-transformer)

### License

**This is a fork.** "Colorwheel for Create Fly" is based on the upstream project [Colorwheel by djefrey](https://github.com/djefrey/Colorwheel), Copyright © 2025 djefrey, which is licensed under the **MIT** license. All of the original code this fork is based on is, and remains, available from upstream under the MIT license; that license, together with the MIT notice for the Flywheel-derived code, is preserved in the [`licenses/`](licenses) directory.

"Colorwheel for Create Fly" as a whole is licensed under the **GNU Affero General Public License version 3** (AGPL-3.0-only); see [`LICENSE`](LICENSE), with copyright holders listed in [`NOTICE`](NOTICE). You are free to read, distribute and modify the code under the terms of that license.  
This does **not** apply to the shaderpack patches provided in the Releases section.

The relicensing is permitted by the MIT license (which allows sublicensing) and is necessary because this fork links against [glsl-transformer](https://github.com/IrisShaders/glsl-transformer), which is itself licensed under the AGPL-3.0. Since the combined work incorporates AGPL-3.0 code, the fork as a whole must be distributed under the AGPL-3.0.

This project is also partially based on code from Flywheel, which is licensed under the MIT license.
