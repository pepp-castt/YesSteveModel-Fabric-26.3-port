# YSM Fabric 26.3 Porting Log

## Baseline

- Upstream architecture source: YesSteveModel/YesSteveModel
- Fabric compatibility baseline: LINGYAN2021/YesSteveModel-Fabric, `port/fabric-26.1.2`
- Port branch: `port/fabric-26.3`
- Target Minecraft: `26.3`
- Target Fabric Loader: `0.19.5`
- Target Fabric API: `0.160.6+26.3`
- Target Loom: `1.17.21`
- Target Gradle: `9.6.0`
- Target Java: `25`

## Compatibility Map — confirmed findings

| Component | Minecraft-sensitive | Fabric-sensitive | Native-sensitive | 26.3 action |
|---|---:|---:|---:|---|
| LivingEntity.blockedByItem | Yes | No | No | PATCHED: 26.3 adds DamageSource, damage, fullyBlocked |
| LevelRenderer render hook | Yes | No | No | PATCHED: renderLevel was replaced by render(...) |
| ProjectionMatrixBuffer GPU slice type | Yes | No | Yes | PATCHED: moved to RenderPearl GpuBufferSlice |
| GpuBufferSlice mixin target | Yes | No | Yes | PATCHED: target RenderPearl slice class |
| EntityRenderDispatcher.extractEntity | Yes | No | No | VERIFIED descriptor matches 26.3 |
| EntityRenderDispatcher.submit | Yes | No | Yes | VERIFIED descriptor matches 26.3 |
| EntityRenderer.submit | Yes | No | Yes | VERIFIED descriptor matches 26.3 |
| EntityRenderer.createRenderState(Entity,float) | Yes | No | No | VERIFIED descriptor matches 26.3 |
| GameRenderer.renderLevel | Yes | No | Yes | VERIFIED method still exists |
| KeyboardHandler.keyPress | Yes | No | No | NOT YET FULLY AUDITED |
| MouseHandler.onButton | Yes | No | No | NOT YET FULLY AUDITED |
| ServerPlayer.startRiding | Yes | Fabric | No | NOT YET FULLY AUDITED |
| Entity capability save/load | Yes | Fabric | No | NOT YET FULLY AUDITED |
| Projectile ownership hooks | Yes | No | No | NOT YET FULLY AUDITED |
| GUI/PiP rendering | Yes | Fabric | Yes | NOT YET FULLY AUDITED |
| BufferBuilder / vertex submission | Yes | Yes | Yes | NOT YET FULLY AUDITED |
| Native JNI boundary | No | No | Yes | REQUIRES PLATFORM-BINARY TEST |

## Known previous failure

The earlier 26.3 attempt failed during Mixin transformation of `net.minecraft.world.entity.LivingEntity`.

The verified root cause candidate is the stale `LivingEntityShieldMixin` hook:

- 26.1.2 source targeted `blockedByItem` with only an attacker parameter.
- 26.3 target signature is `blockedByItem(LivingEntity, DamageSource, float, boolean)V`.
- The patch updates the Mixin descriptor and callback signature accordingly.
- This must be revalidated at runtime; source correction alone is not a pass.

## Render pipeline findings

Minecraft 26.3 uses the state/extract/submit render architecture already anticipated by the 26.1.2 Fabric fork.

Important 26.3 changes verified against the actual game bytecode:

1. `EntityRenderDispatcher.extractEntity(Entity,float)` exists.
2. `EntityRenderDispatcher.submit(EntityRenderState,CameraRenderState,double,double,double,PoseStack,SubmitNodeCollector)` exists.
3. `EntityRenderer.submit(EntityRenderState,PoseStack,SubmitNodeCollector,CameraRenderState)` exists.
4. `EntityRenderer.createRenderState(Entity,float)` exists.
5. `LevelRenderer.render(...)` replaced the old `LevelRenderer.renderLevel(...)` hook.
6. `ProjectionMatrixBuffer.getBuffer(Matrix4f)` and its `writeBuffer(Matrix4f)` helper now return `com.mojang.renderpearl.api.buffers.GpuBufferSlice`.

These changes are treated as compatibility patches, not bypasses.

## Test-state terminology

- PASS = executed and verified in the stated environment.
- FAIL = reproduced and currently unresolved.
- NOT VERIFIED = not yet executed.
- NOT SUPPORTED = intentionally excluded from the release candidate.

A successful Gradle JAR build alone is never treated as release readiness.
