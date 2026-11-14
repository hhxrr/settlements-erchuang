# 二次创作声明 / NOTICE

## 上游

本仓库代码 fork / 二次创作自：

- **项目**：[breeze-devs/Settlements-Alpha](https://github.com/breeze-devs/Settlements-Alpha)
- **说明**：A Minecraft mod that enhances village-related experiences
- **代码许可**：GNU General Public License v3.0（[LICENSE](LICENSE)）
- **素材许可**：CC BY 4.0（[LICENSE-ASSETS](LICENSE-ASSETS)），署名 "Breeze and the Settlements contributors"
- **作者名单**：[AUTHORS](AUTHORS)；第三方素材来源：[CREDITS.md](CREDITS.md)

上游的许可证、作者与贡献者声明在本仓库中**原样保留**。

## 本二创的改动

由 `hhxrr` 在沙箱环境中完成，主要包括（详见 [README_机械动力改造.md](README_机械动力改造.md)）：

1. 机械动力（Create）兼容：创造板条箱供料、机械摆放、蓝图大炮占位。
2. 村民行为：放置/破坏方块、交易换房自动选址建造、建并运转工厂、扩建村庄、修铁路、造火车/火车站、地形修补、搭建飞艇。
3. 新增建筑师 / 工厂工人 / 机械师三个职业及对应工作站方块，接入交易菜单。
4. 乡村生活：山东农村风房屋、结婚同居、家具方块、炊烟、每日作息、机械动力城墙。
5. 模型对话建造（AI 村民）：推理配置、解析机制、离线兜底、建造执行、网络与界面。
6. 聚落自动升级（村庄 → 小镇 → 城市）＋ 柴油动力、公交、地铁兼容。
7. 世界生成整村山东民房 → 全村拆除重建 → 真实砖瓦城市。
8. 贴地修复、三合院 + 天花板、真实几何材质家具、井字村道、村民手持方块逐格搭建。

编译方式为在本地绕过反编译问题后重新编译、更新 `settlements-1.0.0-beta.nightly-110126-机械动力改造.jar`，并在 HMCL 的 15 Mods 环境中实机验证。

## 许可沿袭

依据 GPL-3.0 的传染性，本二创的**全部代码改动继续以 GPL-3.0 提供**，源码即本仓库。
任何基于本仓库的再分发都必须：

- 同样以 GPL-3.0 开源并提供完整源码；
- 保留本 NOTICE、上游 LICENSE / LICENSE-ASSETS / AUTHORS / CREDITS.md；
- 明确标注修改来源与日期。

## 第三方组件

Release 整合包 `万家烟火3.0.zip` 中随附的第三方 Mod 版权归各自作者所有，各自许可独立适用，本二创不主张任何权利：

| Mod | 说明 |
| --- | --- |
| Create | 机械动力（运行时必需依赖） |
| Create: Aeronautics | 飞艇 / 船（可选） |
| Valkyrien Skies 2 | 物理引擎（可选） |
| Create Diesel Generators | 柴油动力（可选） |
| Sable / Struts | 航空学相关依赖 |

## 关系说明

本仓库为**个人二次创作**，与上游 breeze-devs 无隶属、无 endorsement 关系。
