# CRglTF-Example
Example usages for [CRglTF](https://github.com/CleanroomMC/CRglTF).

To get a PBR rendering result like preview below, please follow this [tutorial](https://github.com/CleanroomMC/CRglTF/blob/master/docs/enabling_pbr_material_of_gltf_models_through_shader_pack.md).

> The CRglTF mod isn't available on any maven yet, please download the JAR file from the [Release](https://github.com/CleanroomMC/CRglTF/releases) and put in `libs` folder.

![preview](preview.jpg)
## Model Source
https://github.com/KhronosGroup/glTF-Sample-Assets

- Boom Box and Water Bottle by Microsoft
- Cesium Man by Cesium

## Model Modifications
- In order to adapt into Minecraft's Resource Location system, all the URIs and file names were changed from Camel case to snake_case.
- Some models have changed their node transformations in order to fit proper location. 
