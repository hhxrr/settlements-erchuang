# settlements 二创 · 万家烟火 3.0

> **Settlements-Alpha 二次创作版** —— 村民会自己盖三合院、铺井字村道、逐格搬砖的 Minecraft 模组。
> 基于 NeoForge 1.21.1，实机 15 Mods 环境零崩溃验证。

| 项目 | 内容 |
| --- | --- |
| 上游项目 | [breeze-devs/Settlements-Alpha](https://github.com/breeze-devs/Settlements-Alpha) |
| 本仓库 | `hhxrr/settlements-erchuang`（GitHub 仓库名不支持中文，故 URL 用拼音；中文名见标题/描述/Release） |
| 游戏版本 | Minecraft 1.21.1 · NeoForge |
| 主要依赖 | Create（机械动力，必需）、Create: Aeronautics、Valkyrien Skies 2（可选） |
| 代码协议 | **GPL-3.0**（沿用上游，见 [LICENSE](LICENSE)） |
| 素材协议 | CC BY 4.0（见 [LICENSE-ASSETS](LICENSE-ASSETS)） |
| 成品下载 | **Releases → `WanJiaYanHuo-3.0.zip`**（即「万家烟火3.0」整合包 + 实测视频 + 截图 + 文档） |

---

## 一眼看懂：这一版改了什么

| # | 改造 | 效果 |
| --- | --- | --- |
| 1 | **整村山东民房** | 世界生成时按村庄规模铺满成片院落，超平坦世界可直接看全村 → 小镇 → 城市 |
| 2 | **完美贴地** | 重写地形采样：在村庄结构之外 8 方向取中位数，清挖从 `terrainY+1` 起，彻底解决旧版房子悬空 / 遁地 |
| 3 | **三合院** | 15×15 院落：南面门楼入院，正房（两层变体）+ 西厢房 + 东厢房，中庭 + 石板甬道 + 青灰院墙瓦顶 |
| 4 | **天花板** | 每栋房墙顶铺整片云杉木平整顶棚；放置队列按 y 从低到高排序，逐格搭建时能看到「地板→墙→天花板→青瓦坡顶」 |
| 5 | **真实材质模型** | 家具从 `cube_all` 木板盒改为纯 JSON 立体几何：X 形交叉腿折叠凳、折叠桌、茶壶茶杯、折扇、摊开的扑克牌；房屋换青灰石砖墙 / 泥砖墙裙 / 木圈梁 / `deepslate_tiles` 青瓦 |
| 6 | **井字村道** | 抛光安山岩亮色中心 + 圆石路缘 + 石砖十字路口 + 砂砾变化 + 交叉口灯笼路灯，网格步长与三合院占地对齐 |
| 7 | **村民逐格搭建** | 8 个真实村民贴地站工地外圈，手持当前正在放置的方块并挥臂，`ItemDisplay` 同步显示；`/scaledemo yard` 近景演示，6 格/tick 约 12 秒可看清全过程 |
| 8 | **机械动力能力** | 放置/破坏方块、交易换房自动选址建造、建并运转 Create 工厂、扩建村庄、修铁路、造火车与火车站、地形修补、搭建飞艇 |
| 9 | **聚落自动升级** | 按人口自动 村庄 → 小镇 → 城市；柴油动力、公交、地铁附属兼容 |

完整的七轮改造记录（含根因分析、文件清单、命令用法）见 **[README_机械动力改造.md](README_机械动力改造.md)**。

---

## 📖 完整教程

**[《万家烟火 3.0 · 使用与二创教程》](docs/使用与二创教程.md)** —— 覆盖两条路线：
玩家从装整合包到 `/scaledemo yard` 看村民逐格搭建，开发者从源码构建到七轮改造定位。
含命令速查卡、验收清单、FAQ、整合包目录树与实测视频索引。

---

## 快速开始

### 方式 A：直接玩整合包（推荐）

去 [Releases](https://github.com/hhxrr/settlements-erchuang/releases) 下载 **`WanJiaYanHuo-3.0.zip`**（中文名「万家烟火 3.0」，因 GitHub Release 资产名不支持中文故用拼音；解压后顶层目录为 `万家烟火3.0/`），内含：

- `mods/` —— 实测通过的 Mod 组合与本二创 jar
- `实测视频/` —— 8 段实录（含 `07_贴地三合院_天花板_真实材质_村民逐格搭建.mp4`）
- `实测截图/` —— HMCL 安装、Mods 列表、世界内实拍
- `文档/` —— 改造说明、交付清单、使用说明

用 HMCL 或任意支持 NeoForge 1.21.1 的启动器，把 `mods/` 内容放入实例的 `mods/` 目录即可。

### 方式 B：从源码构建

```bash
git clone https://github.com/hhxrr/settlements-erchuang.git
cd settlements-erchuang
# 需要完整的 Create 6.x (mc1.21.1) jar 放到 libs/（本仓库不分发第三方 jar）
# 详见 README_机械动力改造.md 的「构建 / 测试」章节
./gradlew build
```

产物在 `build/libs/`，丢进整合包 `mods/` 目录开世界测试。

### 游戏内演示命令

| 命令 | 作用 |
| --- | --- |
| `/scaledemo yard` | 近景：单个三合院逐格搭建（村民手持方块，约 12 秒） |
| `/scaledemo village` | 整村山东民房 |
| `/scaledemo town` / `city` | 小镇 / 城市升级 |
| `/scaledemo status` | 查看当前搭建队列进度 |

---

## 实测结论

- HMCL 加载 **15 Mods** 全程 **零崩溃**，实跑 `/scaledemo yard` 入队 2359 格慢速逐格落地。
- 最终 `settlements-1.0.0-beta.nightly-110126-机械动力改造.jar`（5,037,901 字节）在根目录、mods 目录与两个交付包共 4 处 md5 完全一致。
- 全部家具模型为真实立体几何，无紫黑缺失材质；俯视整村全景无未加载空洞。

---

## 协议与署名（重要）

本仓库是 [breeze-devs/Settlements-Alpha](https://github.com/breeze-devs/Settlements-Alpha) 的**二次创作**。

- **代码**：上游使用 **GNU GPL v3.0**，本二创**继续以 GPL-3.0 开源**，任何再分发必须同样开源并提供源码。见 [LICENSE](LICENSE)。
- **美术 / 音效 / 模型素材**：上游使用 **CC BY 4.0**，署名 "Breeze and the Settlements contributors"，具体作者见 [AUTHORS](AUTHORS) 与 [CREDITS.md](CREDITS.md)。见 [LICENSE-ASSETS](LICENSE-ASSETS)。
- **第三方 Mod**（Create、Create: Aeronautics、Valkyrien Skies 2、柴油动力、Sable、Struts 等）版权归各自作者，**不在本仓库源码中分发**；Release 整合包中仅按各自许可随附，使用前请遵守其原始许可。
- 上游版权与贡献者声明完整保留，未做删改。二次创作补充说明见 [NOTICE.md](NOTICE.md)。

---

## 反馈

欢迎提 [Issue](https://github.com/hhxrr/settlements-erchuang/issues)。本仓库为个人二次创作，与上游 breeze-devs 无隶属关系。
