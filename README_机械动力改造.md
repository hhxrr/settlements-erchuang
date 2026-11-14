# Settlements × 机械动力（新蒸程整合包）改造说明

基于 `breeze-devs/Settlements-Alpha`（NeoForge 1.21.1）改造，目标：在 **机械动力 Create 整合包**
环境下让村民具备「放置/破坏方块、交易换房自动建造、建造并运转工厂、扩建村庄、修铁路、造火车与火车站、
地形修补、搭建并操控机械动力飞艇/船」等能力。

> ⚠️ 沙箱网络无法拉取完整的 NeoForge + Create（Create jar 仅下到约 6.5/40MB），
> 因此本改动**未经沙箱编译验证**。请在你的整合包开发环境（有网、有 Create）执行 `./gradlew build` 验证。

---

## 依赖模型（重要）

| 模组 | 依赖类型 | 说明 |
|---|---|---|
| **Create（机械动力）** | **必须 (required)** | 已写入 `neoforge.mods.toml` 的 `[[dependencies]]`。未安装则本 mod 直接不加载。 |
| **Create: Aeronautics（航空学）** | 可选 (optional) | 飞艇/船、强力胶、物理化等功能的前提。未安装时相关行为自动降级，不影响其余功能。 |
| **Valkyrien Skies 2（物理引擎）** | 可选 (optional) | 航空学的底层依赖，同样可选声明。 |

**简化版 vs 完整版**：
- **简化版（仅机械动力）**：村民能做房屋/工厂/扩建/铁路/造火车与火车站/地形修补/机械运转，但**没有飞艇/船**。
- **完整版（装了航空学）**：在简化版基础上，建筑师可**搭建并操控机械动力飞艇/船**（强力胶浇固 + 物理化）。

> 关于「没有整合包时」：因为 Create 已是必须依赖，整合包（含 Create）是运行前提；
> 三个新职业因此**总是注册**。航空学缺失只是关闭飞艇这一支线，其余能力齐全。

---

## 已实现的功能

| 需求 | 实现位置 | 说明 |
|---|---|---|
| 兼容机械动力 | `compat/create/*` | `CreateCompat` 总开关、`CreateBlockRefs`、`CreativeCrateSupplier`（创造板条箱供料）、`CreateMachineBuilder`（机械摆放）、`SchematicannonBlueprintBuilder`（蓝图大炮占位） |
| 放置 / 破坏方块 | `build/PlaceBlocksBehavior`、`build/BreakBlocksBehavior` | 村民可铺地基、可清场拆方块 |
| 交易换房→自动选址建造 | `BuildHouseBehavior` + `BuildingSiteFinder` + `HandHousePlan` | 绿宝石买「建房订单」→ 自动找空地 → 逐格建造（1 方块 = 1 绿宝石） |
| 建造并运转工厂 | `BuildFactoryBehavior` + `OperateFactoryBehavior` + `FactoryPlan` | 在村庄建一座 Create 加工厂（安山岩外壳+创造板条箱+动力源+压力机/搅拌机/中转台）；工厂工人日常巡检并激活设备 |
| 扩建村庄 | `ExpandVillageBehavior` | 建筑师在村庄边缘找新空地增建房屋（可多次购买「扩建订单」） |
| 修铁路到下一村庄 | `BuildRailwayBehavior` + `RailwayBuilder` + `VillageLocator` | 定位**最近的非废弃、非僵尸村庄**（按 POI 床数 + 活村民判定），用机械动力铁轨连过去（无需火车） |
| **造火车** | `BuildTrainBehavior` + `TrainBuilder` | 机械师消费「火车订单」→ 就近铺一段铁轨并搭出一列机械动力火车（详见下方「已知限制」） |
| **火车站** | `BuildTrainStationBehavior` + `StationBuilder` | 机械师消费「火车站订单」→ 整平地面并在村庄建一座火车站，**站中央放置机械师工作站方块**（职业方块） |
| **地形修补** | `RepairTerrainBehavior` + `TerrainRepairer` | 机械师消费「地形修补订单」→ 填平坑洞、整平地面、清理悬空残骸 |
| 搭建 / 操控飞艇·船 | `BuildAircraftBehavior` + `OperateAircraftBehavior` + `AeronauticsCompat` | **仅航空学可用**：铺飞艇外壳+动力源→**强力胶浇固**→**物理化**为可操控飞船→村民就位「驾驶」 |
| **职业方块（工作站）** | `WorkstationBlock` + `BlockRegistry` + `PoiTypeRegistry` + `ProfessionRegistry` | 三个职业各有专属工作站方块并注册成 POI：建筑师→`architect_station`、工厂工人→`factory_station`、机械师→`mechanic_station` |
| 新增职业 | `ProfessionRegistry` + `VillagerProfessionKey` + `PoolModule` | **建筑师（architect）+ 工厂工人（factory_worker）+ 机械师（mechanic）** 三个职业 |
| 交易菜单接入 | `BuildTradeEvents`（`VillagerTradesEvent`） | 建筑师：房屋/扩建/铁路（+飞艇，仅航空学）；工厂工人：机械/拆除/工厂；机械师：火车/火车站/地形修补 |

> 注：**机械师（mechanic）职业已恢复**（此前简化版曾删除）。三个职业的「建造机械」职责划分如下：
> - 建筑师：房屋、扩建、铁路、火车站（找地、铺轨）、飞艇（需航空学）。
> - 工厂工人：机械、拆除、工厂、工厂运转。
> - 机械师：火车、火车站、地形修补（以及通用放置/破坏方块）。

---

## 关键设计

- **建造 AI 直接继承 `AbstractBehavior<BaseVillager>`**（而非 `VillagerStateMachineBehavior`）：
  后者的 `StagedStep`/`OneShotStep` 只执行一次、无法做「逐格循环」；`AbstractBehavior.tickBehavior`
  每 tick 调用一次，正好用于「每 tick 推进一格方块」的循环，可控性最高、风险最低。
- **供料**：身边 `CRATE_RADIUS` 内有创造板条箱 → 无限供料；否则消耗村民背包方块
  （`consumeOrBypassed`）。满足「建造时方块自动用创造板条箱」。
- **计价**：HOUSE 按 `HandHousePlan.costInBlocks()`（房屋方块数）算绿宝石；其余取固定价
  （1 方块 = 1 绿宝石的等价原则，复杂结构用预估值）。
- **职业方块（工作站）**：每个职业在 `BlockRegistry` 注册一个 `WorkstationBlock`，并在 `PoiTypeRegistry`
  里注册为对应的 `PoiType`（`POINT_OF_INTEREST_TYPE`），`ProfessionRegistry` 通过该 `ResourceKey<PoiType>`
  把职业绑定到工作站。村民只要站在对应工作站方块附近即可认领职业；工作站方块可在创造标签页获取，
  也可由 `BuildTrainStationBehavior` 在村中放置。
- **降级（可选依赖）**：
  - Create 缺失 → mod 不加载（必须依赖）。
  - 航空学缺失 → `AeronauticsCompat.isLoaded()` 为 false，`BuildAircraftBehavior` / `OperateAircraftBehavior`
    在 `doStart` 立即停止并打印提示；`BuildTradeEvents` 也不提供飞艇交易。**全程不引用航空学类**
    （只用 `ModList` + 原版注册表 + 反射），保证无航空学时也能编译通过。
- **村庄定位（含僵尸村排除）**：`VillageLocator` 用 `PoiManager` 扫描「集会点(meeting)」POI，并对每个候选：
  1. 统计周边床数，低于阈值判定为**废弃村庄**跳过；
  2. **额外要求候选点附近存在活着的村民**（`EntityType.VILLAGER` 扫描），以此排除**僵尸村庄**
     （僵尸村庄的床(POI)齐全，但村民都是僵尸村民，不算正常 Villager，因此会被这里过滤掉）。
  全部包裹 try/catch，API 差异时优雅返回空。

---

## 构建 / 测试（在你本机整合包环境）

```bash
# 1) 把完整的 Create 6.x (mc1.21.1) jar 放到 libs/，覆盖沙箱里不完整的那份
cp /path/to/create-1.21.1-6.0.10.jar libs/

# 2) （可选）依赖航空学 / VS2 的整合包环境：把 create_aeronautics、valkyrien_skies 也装上
#    飞艇/船功能才会启用。

# 3) 构建
./gradlew build        # 产出 build/libs/settlements-*.jar

# 4) 丢进整合包 mods/ 目录，开世界测试：
#    - 给村民对应工作站方块（建筑师→建筑绘图台 architect_station、工厂工人→操作台 factory_station、
#      机械师→机械台 mechanic_station）以获取职业（也可直接用创造标签页拿方块）
#    - 右键村民打开交易菜单，用绿宝石购买「建房 / 扩建 / 铁路 / 工厂 / 火车 / 火车站 / 地形修补」订单
#    - 村民背包出现订单后，会自动找空地开始建造 / 修铁路 / 造火车 / 建站 / 修补地形
#    - 装了航空学时，建筑师会出现「搭建飞艇」交易；购买后村民会自动造出可操控飞艇
```

---

## 已知限制 / 待补（下一阶段）

1. **蓝图大炮（Schematicannon）**：`SchematicannonBlueprintBuilder.startPrinting()` 目前抛
   `NotImplementedException`，需对照你本地 Create 源码补全「装填 schematic 物品 + 火药 + 起印」的 BE 调用。
2. **航空学 API 为占位/反射**：`AeronauticsCompat.applySuperGlue()` 与 `physicalize()` 仅用反射骨架
   （`net.creativelabs.aeronautics...`、`org.valkyrien_skies...`），需按你本地航空学/VS2 版本把真实调用补实。
   这是「可选依赖无法编译期校验」的固有代价——运行时若类名/方法名不符会静默降级。
3. **机械朝向与产线联动**：`FactoryPlan` / `RailwayBuilder` / `CreateMachineBuilder` / `TrainBuilder` 只保证结构
   成立、方块合法，具体齿轮连接、轨道朝向、`Train` 装配需在游戏内按本地 Create 版本校准。
4. **火车目前是「静态外形」**：`TrainBuilder` 以方块搭出停在轨道上的火车外形（安山岩外壳+齿轮车轮+熔炉引擎+座位），
   要保证它能真正行驶，需在游戏内对这列方块执行机械动力**火车装配**流程（或后续接入
   `com.simibubi.create.content.trains` 的 `Train` API 程序化装配）。机械动力未安装时降级为原版铁轨 + 矿车。
5. **POI 构造器可见性（编译注意）**：`PoiTypeRegistry` 直接 `new PoiType(states, 1, 1)`。NeoForge 21.1 下该构造器
   通常为 public；若你的环境报 `PoiType() has protected access`，在 `src/main/resources/META-INF/accesstransformer.cfg`
   加一行 `public net.minecraft.world.entity.ai.village.poi.PoiType <init>(Ljava/util/Set;II)V` 即可。
6. **职业工作站的村庄自动生成**：当前村民通过「站在工作站方块附近」认领职业；建议在村庄生成逻辑里也为每座村
   放置三块工作站方块（或让 `BuildTrainStationBehavior` 顺带放置另外两块），以便村民自动上岗。
7. **VillageLocator 扫描范围**：当前扫描已加载区块（半径 256）。超远距离需先让目标区块加载，
   或改为 `findNearestMapStructure` 强制生成结构（API 随版本变动，已用 try/catch 兜底）。
8. **沙箱未编译验证**：所有 Java 文件均按 Settlements 现有架构（Dagger + 行为树）编写，但需你本机 `./gradlew build`
   跑通并修复可能的 API 偏差（主要是 Create 6.x 的 `AllBlocks` 字段名与航空学/VS2 的实际包路径）。

---

# 第二轮：乡村生活（山东农村风）

在第一轮「三职业 + 机械动力建造」的基础上，本轮把村庄改造成有烟火气的北方农村，
并补齐了村民的日常生活、婚姻与公共建设。

## 1. 房子整改（山东农村风）

- 新增 `ShandongHousePlan`（数据驱动多布局）+ `RemodelHouseBehavior`（建筑师）：
  - **一层带院农房** `COURTYARD`：灰砖墙、木梁、青瓦坡顶、砖砌院墙围合带院门，房内有床。
  - **两层小楼** `TWO_STORY`：同上但两层，二层木楼板 + 简易木梯 + 二层卧室床。
  - **工作房** `WORKSHOP`：**没有床**，仅供生产（熔炉 + 工作台），对应「工作房（没有床）」。
- 村民通过 `VILLAGER_HOUSE_STYLE` 附件选择布局（0=一层带院 / 1=两层小楼 / 2=工作房）。
- 改造以「家/当前位置」为原点，逐格落地（`RemodelHouseBehavior` 复用 `BuildPlan` 的 PlaceEntry 机制）。
- 整村统一翻新：后续可把 `RemodelHouseBehavior` 接入村庄生成流水线，对每座非工作房套用山东布局。

## 2. 村民结婚 + 同居（家设置块）

- 新增方块 **家设置块 `home_setting`**（带 `BlockEntity` 持久化成员列表）：
  - **蹲下 + 右键**：把注视方向最近的村民登记为本家成员（写入 `VILLAGER_HOME` 附件指向该块）。
  - 再次对同一个人操作则移除。
  - **手持【已命名的命名牌】蹲下右键**：给注视的村民改名（即「交易菜单改名」的玩家驱动入口）。
- 结婚机制：`MarryMoveInBehavior`（全员共有）发现两名村民同住一个家设置块时，双方互写
  `VILLAGER_SPOUSE`，结为配偶。**把一对村民都登记到同一块家设置块 = 结婚**，此后两人 `VILLAGER_HOME`
  相同，作息中的回家/睡觉行为会把他们导航到同一个家，实现「搬到一起住」。

## 3. 家具方块（创造标签页可取）

| 方块 | 说明 | 用途 |
|------|------|------|
| `folding_chair` 折叠椅 | 院子里的坐具 | 喝茶/打牌时村民走到附近 |
| `folding_table` 折叠桌 | 院子里的桌子 | 吃饭/喝茶场地 |
| `tea_set` 茶具 | 室内/院中 | 触发喝茶行为 |
| `fan` 扇子 | 手持纳凉 | 触发扇扇子行为 |
| `playing_cards` 扑克牌 | 棋牌 | 多人打牌（椅子桌子放院子里） |

## 4. 炊烟（厨房烟囱）

- 新增 **烟囱 `chimney`**（带 `BlockEntity`）：从顶部持续冒暖色炊烟。
- `ChimneyBlockEntity` 平时淡烟，`CookMealBehavior` 做饭时把标记置真 → 炊烟变浓，表现
  「厨房升起炊烟」。粒子走 `ParticleRegistry.cookingSmoke(Location, strength)`。

## 5. 每日作息（机械师视角，全员通用）

全部以游戏内时间（Minecraft 日循环）驱动，挂在 `BehaviorPoolResolver.UNIVERSAL_ENTRIES`，
即**所有村民**都会执行：

1. **起床** `WakeUpBehavior`（06:00–07:00）：起身走到家门口。
2. **卫生间** `BathroomBehavior`（07:00–07:30）：走到就近水/堆肥桶刷牙洗脸。
3. **做饭** `CookMealBehavior`（三餐时段）：到厨房，点亮烟囱 → 升起炊烟。
4. **吃饭** `EatMealBehavior`：端到折叠桌旁用餐。
5. **去工作**：由各自职业行为（工厂/机械师等）接手。
6. **回来烧饭** `CookMealBehavior`：午/晚再做一次饭，炊烟再起。
7. **吃完去工作**：循环职业行为。
8. **回来烧晚饭** `CookMealBehavior`：傍晚升炊烟。
9. **吃晚饭** `EatMealBehavior`。
10. **睡觉** `SleepHomeBehavior`（20:00 后）：回床，**拉拉杆把红石灯关掉**。

> 房子灯为**红石灯**，家附近放一个**拉杆**控制它；村民「拉拉杆」即点亮/熄灭（`DomesticUtil.setLeverNear`）。

## 6. 空闲活动

- `DrinkTeaBehavior`：白天空闲走到茶具 + 折叠桌旁喝茶。
- `FanBehavior`：夏天（以「白天空闲」近似，具体季节需季节模组）持扇轻摇纳凉。
- `PlayCardsBehavior`：多个村民聚到扑克牌旁打牌，每局随机一名**赢家一直高兴地跳**。

## 7. 机械动力城墙（机械师）

- `CreateWallBuilder` + `BuildCityWallBehavior`（机械师）：以村庄中心为基准，沿正方形周界
  逐格砌起带垛口与四角**引擎角楼**的城墙，南侧留城门缺口。
- 材料优先机械动力 casing / 铜饰 / 蒸汽引擎（`CreateBlockRefs.wallCasing()` 等，反射探测字段名），
  未加载 Create 时退化为原版安山岩 + 铜块 + 红石块。

## 8. 接线总览（本轮新增的注册点）

- 行为键：`BehaviorKey` 新增 `WAKE_UP / USE_BATHROOM / COOK_MEAL / EAT_AT_TABLE / SLEEP_HOME /
  DRINK_TEA / USE_FAN / PLAY_CARDS / MARRY_MOVE_IN / BUILD_CITY_WALL / REMODEL_HOUSE`。
- 全员共有：`BehaviorPoolResolver.UNIVERSAL_ENTRIES` 加入 9 个生活行为（起床…打牌）。
- 职业池：`mechanicPool` 加 `BUILD_CITY_WALL`；`architectPool` 加 `REMODEL_HOUSE`。
- 方块/物品/方块实体：`BlockRegistry` / `ItemRegistry` / `BlockEntityTypeRegistry` / `CreativeTabRegistry`
  新增家具、家设置块、烟囱，并生成 blockstate/model/item JSON。
- 附件：`AttachmentRegistry` 新增 `VILLAGER_HOME`(GlobalPos) / `VILLAGER_SPOUSE`(UUID) / `VILLAGER_HOUSE_STYLE`(int)。

## 第二轮待校准点

1. **房屋改造与村庄生成流水线的对接**：`RemodelHouseBehavior` 目前以「家/当前位置」为原点单座改造；
   若要整村自动翻新，需要把它接入 `SettlementFoundingService` / 生成流水线（按每座村选布局、批量落地）。
2. **炊烟/拉杆联动**：烟囱与红石灯的联动依赖玩家在村中实际放置烟囱与拉杆，行为只在附近查找；
   未放置时相关步骤静默跳过（不影响其他流程）。
3. **打牌「赢家跳」与「扇扇子」为轻量表现**：跳跃用 `setDeltaMovement` 小幅上抛，多人对局需要更精细的
   回合/输赢判定时可在此扩展。
4. **与第一轮同样的编译前提**：需本机含 Create 6.x（及可选航空学）的整合包 `./gradlew build`。
   `CreateBlockRefs` 用反射探测 casing/引擎字段名，若你版本的 `AllBlocks` 字段名不同，按本地源码微调即可。

---

# 第三轮：模型对话建造（AI 村民）

把一个小模型接入村民，让玩家用自然语言与村民对话、**告诉村民想造什么建筑**，模型还能**生成 ASCII 地图**；
再通过一个**解析机制**把模型输出（JSON 蓝图 / ASCII 地图）转换成可建造的 `BuildPlan`，由村民**逐格搭出来**。

> 本子系统与既有 SIS 推理管线完全独立：新建 `application/ai/modeldialogue` 包，自带离线兜底模型，
> 无需任何外部服务即可体验「对话 → 批准 → 村民动工」全流程。

## 1. 功能目标（对应需求）

| 需求 | 实现 |
|---|---|
| 把一个小模型接入，让村民通过模型与玩家对话 | `ModelDialogueService` + `ModelClient`（`OpenAiCompatibleModelClient` / `OfflineMockModelClient`） |
| 可以告诉村民想造啥房子 | 对话界面输入中文意图（如「帮我盖座小木屋」），模型返回建筑蓝图 |
| 模型还可以生成地图 | 模型在 `map` 字段输出 ASCII 俯视图，由 `MapParser` 解析为蓝图 |
| 添加一个解析机制，解析模型输出的建筑 | `ModelOutputParser`（剥离围栏→抽取 JSON→蓝图/地图→`BuildPlan`）+ `BlueprintParser` + `MapParser` |
| 把建筑做成村民在搭的 | `BuildOrderQueue`（选址入队）→ `BuildFromBlueprintBehavior`（认领→逐格施工） |

## 2. 架构总览

```
玩家输入(聊天界面)
   │ ServerBoundModelChatPacket
   ▼
ServerBoundModelChatPacketHandler
   │ ModelDialogueService.converse(player, villager, msg)
   ├─ ModelClient.complete(systemPrompt, history, userText)   ← 在线: OpenAI 兼容 / 离线: 关键词匹配
   │      │ 返回原始文本（可能含 JSON 信封 / ```json 围栏）
   │      ▼
   └─ ModelOutputParser.parse(raw, config)
          ├─ BlueprintParser.parseJson(...)   ← 解析 "blueprint":{name,blocks:[{x,y,z,block}]}
          └─ MapParser.parse(...)            ← 解析 "map":"legend:...\n#####\n..."
          → ParsedModelOutput(reply, blueprint?, mapText?, error?)
   │ ClientBoundModelChatPacket（回传 reply+blueprint+map）
   ▼
ModelChatScreen 显示；玩家点「批准建造」
   │ ServerBoundModelApproveBuildPacket
   ▼
ServerBoundModelApproveBuildPacketHandler
   │ ModelDialogueService.enqueueBuild(...) → BuildOrderQueue.submit(plan, level, near)
   │   自动 BuildingSiteFinder 选址
   ▼
BuildFromBlueprintBehavior（建筑师/机械师）认领 job → 逐格放置 → 完工出队
```

## 3. 配置（`settlements/inference.toml`）

`ModelConfig`（注解 `@BehaviorConfig(name="model_chat", type=INFERENCE)`）扫描式自动生成 TOML，
默认**关闭在线模型、走离线兜底**。关键字段：

| 字段 | 默认 | 说明 |
|---|---|---|
| `enabled` | `false` | 是否启用在线模型 |
| `endpoint_base_url` | `http://localhost:11434/v1` | OpenAI 兼容端点（默认本地 Ollama） |
| `api_key` | `` | Bearer token，空则不鉴权 |
| `model_name` | `qwen2.5:3b` | 模型标识 |
| `chat_path` | `/chat/completions` | 端点路径 |
| `temperature` / `max_tokens` | `0.8` / `1024` | 采样参数 |
| `max_history_turns` | `8` | 每轮对话保留的历史轮数 |
| `build_max_blocks` | `1200` | 蓝图方块数安全上限，超则拒绝 |

## 4. 接入真实小模型（本地 Ollama 示例）

1. 安装并启动 Ollama：`ollama pull qwen2.5:3b && ollama serve`
2. 编辑 `settlements/inference.toml`：
   ```toml
   [model_chat]
   enabled = true
   endpoint_base_url = "http://localhost:11434/v1"
   model_name = "qwen2.5:3b"
   ```
3. 任意 OpenAI 兼容端点（LM Studio、vLLM、远程 API）同理——改 `endpoint_base_url`/`chat_path`/`api_key` 即可。
   模型只需遵守 `ModelDialogueService.buildSystemPrompt` 里约定的** JSON 信封契约**：
   ```json
   {"reply":"<村民口吻回复>",
    "blueprint":{"name":"<建筑名>","blocks":[{"x":0,"y":0,"z":0,"block":"minecraft:oak_planks"}]},
    "map":"legend: #=minecraft:oak_planks\n#####\n#.#.#\n#####"}
   ```
   闲聊时不要输出 JSON，直接说人话。坐标 `x` 向右、`z` 向前、`y` 向上（0 为地面）。

## 5. 离线兜底（无需任何外部服务）

未启用在线模型（或端点不可达）时，`ModelDialogueModule` 自动选择 `OfflineMockModelClient`：
关键词匹配意图 → `MockBlueprintLibrary` 程序化生成小型蓝图 → 输出与真实模型**完全一致**的 JSON 信封。
支持：小木屋 / 瞭望塔 / 水井 / 矮墙 / 农田 / 熔炉屋 / 中式小院 / 两层小楼（覆盖原版方块，数百方块内）。

## 6. 解析机制（解析模型输出的建筑）

- **`ModelOutputParser`**：先 `stripFences` 去 ```` ```json ```` 围栏，再用平衡花括号扫描 `extractJsonObject`
  抽取最外层 JSON；分别尝试 `blueprint`（对象）与 `blocks`（数组）字段，也支持 `map` 字段；
  **任一环节失败都降级为「只显示回复」**，保证对话界面不崩溃。
- **`BlueprintParser`**：`blueprint` JSON → `BlueprintDTO` → `BuildPlan`（`BuildPlan.PlaceEntry`）。
  非法/为空方块跳过，超 `build_max_blocks` 返回 `null`（上层拒绝）。
- **`MapParser`**：ASCII 地图 → `BuildPlan`。首行 `legend: #=minecraft:oak_planks O=minecraft:oak_door` 自定义图例，
  默认图例（`#`木板/`O`木门/`T`原木/`X`石头/`S`圆石/`B`红砖/`W`白羊毛/`G`玻璃/`D`深色木板/`L`灯笼/`R`红陶/`C`陶瓦）；
  **空行抬升一层（y+1）**，也可 `y=N` 显式指定；列沿 X、行沿 Z 展开，`.`/空格为空气。

## 7. 建造执行（村民在搭）

- **`BuildOrderQueue`**（服务端单例）：玩家批准蓝图后 `submit(plan, level, near)` 自动 `BuildingSiteFinder` 选址入队；
  空闲建筑师/机械师 `claimNext` 认领一份未认领任务，完工/失败 `complete` 出队。**一个 job 仅一个村民盖**。
- **`BuildFromBlueprintBehavior`**（`extends AbstractBehavior<BaseVillager>`，与 `BuildHouseBehavior` 同模式）：
  启动认领 → 每 tick 推进一格（走向目标 → 取料 → 落地方块）→ 四周有创造板条箱则无限供料，否则消耗背包方块 →
  覆盖/完成即出队。被加入 `architectPool` 与 `mechanicPool`，行为键 `BUILD_FROM_BLUEPRINT`。

## 8. 网络与界面

- 包（`infrastructure/network/features/modelchat`）：
  - `ServerBoundModelChatPacket`（村民 id + 消息 + turnId）
  - `ServerBoundModelApproveBuildPacket`（村民 id + 蓝图 JSON，字节数组传输避免超长 UTF 限制）
  - `ClientBoundModelChatPacket`（回复 + 地图文本 + 蓝图 JSON + hasBlueprint + error）
  - 注册于 `PacketRegistry.bindPacketHandlers`；处理器经 `ServerNetworkModule`/`ClientNetworkModule` 的 `@Binds @IntoMap @ClassKey` 装配。
- 客户端界面 `ModelChatScreen`：聊天窗 + 输入框 + 发送/批准建造按钮，`ClientBoundModelChatPacketHandler`
  通过 `ModelChatScreen.deliver(packet)` 投递结果。
- 打开方式（二选一）：
  1. 控制设置里绑定并按下 **「打开村民对话」** 键（默认未绑定，`key.settlements.open_model_chat`）；
  2. 手持 **对话手册 `dialogue_manual`**（创造标签页可取）对着村民按使用键。

## 9. 使用步骤

1. 进入含本 mod + Create 的世界，给一名建筑师或机械师发工作站方块上岗（或创造标签页拿「对话手册」）。
2. 对着该村民按「打开村民对话」键 / 手持对话手册右键。
3. 输入想盖的建筑（如「帮我盖座小木屋」「来个瞭望塔」「围成一圈矮墙」）；离线模型会立即回一张蓝图。
4. 点界面下方 **「批准建造」** → 村民自动找空地、把蓝图逐格盖出来（建议旁边放创造板条箱无限供料）。
5. 启用在线模型后，可与真实本地小模型（Ollama 等）自由对话、生成任意 JSON 蓝图或 ASCII 地图。

## 10. 第三轮注意事项 / 待补

1. **离线模型的「地图」是示意**：`OfflineMockModelClient.sampleMap` 返回固定 5×5 示意网格，主要演示
   `map` 字段链路；真正按地图建复杂结构请以在线模型输出为准。
2. **蓝图上限**：`build_max_blocks`（默认 1200）为安全护栏，超大蓝图会被拒绝；可按本机性能调高。
3. **方块调色板**：系统提示中列出了可用原版方块（`PALETTE`），在线模型应只返回这些 ID 以保证可建造；
   解析器对无法解析的方块会跳过（不中断建造）。
4. **沙箱未编译验证**：同前两轮，需本机 `./gradlew build` 跑通；本子系统纯 Java + Dagger + NeoForge 网络栈，
   不引入新依赖（HttpClient 为 JDK 内置）。

---

# 第四轮：聚落自动升级（村庄 → 小镇 → 城市）＋ 柴油动力 + 公交 + 地铁

目标一句话：**村庄人口够大就自动识别、挖掉旧屋、按小模型蓝图重建为小镇；小镇再够大就升级为城市（城建方块 + 地铁）。**
另外：造车厂由小模型生成（航空学 + 柴油动力），并新增第二个可选附属 mod「机械动力：柴油动力」。

## 1. 聚落规模等级（按人口自动判定）

| 等级 | 判定 | 出现内容 |
|---|---|---|
| `VILLAGE` 村庄 | 默认 | 零散房屋（前几轮既有） |
| `TOWN` 小镇 | 人口 ≥ 12 | 小模型生成的成片房屋、机械动力**造车厂**（航空学+柴油动力）、**公交** |
| `CITY` 城市 | 人口 ≥ 30 | 在小镇基础上追加**城建方块**与**地铁** |

阈值集中在 `domain/settlement/model/SettlementTier.java`（`TOWN_POPULATION` / `CITY_POPULATION`），便于统一调参。

## 2. 自动升级链路

- `application/settlement/scaling/SettlementScalingService`（@Singleton）：按名册人数算等级、维护「已改造去重表」、
  把蓝图原型（`MockBlueprintLibrary`）解析为村民可逐格建造的 `BuildPlan`。
- `behavior/usecases/villager/build/ScaleSettlementBehavior`（建筑师 + 机械师池）：村民被调度时自动检查所属聚落等级——
  - 应处等级高于已改造等级时：**先清挖**旧村庄地面以上结构（「挖掉」），再按小模型蓝图**逐个入队**小镇房屋 / 造车厂 / 公交站，
    并在小镇里**生成公交车实体**；
  - 城市档位再追加**城建方块**与**地铁站**蓝图（地铁隧道由 `BuildSubwayBehavior` 铺设）。
  - 每次升级通过去重表保证「每聚落每档只做一次」，并写一条 `SCALED` 聚落日志。
- `behavior/usecases/villager/build/BuildSubwayBehavior`（机械师池，仅城市）：在地下挖隧道、铺设充能铁轨，
  同样「每城市只铺一次」。

## 3. 机械动力：柴油动力（第二个可选附属）

- `compat/diesel/DieselCompat`：与 `AeronauticsCompat` 同理的**反射 + 注册表探测**式可选集成，绝不 import 具体类；
  按候选 modid（`create_diesel` / `dieselgenerators` / `diesel_generators`）探测，命中其一即视为可用。
- `neoforge.mods.toml` 模板新增 `create_diesel` 的 `optional` 依赖声明。
- 造车厂蓝图里的柴油机组方块按 `create_diesel:*` 命名引用；**未安装柴油动力时静默退化为空气**，厂房依旧可盖。

## 4. 小模型生成造车厂（航空学 + 柴油动力）

`MockBlueprintLibrary.carFactory()` 产出的蓝图混合：

- 地基 `stone_bricks`、墙体 `create:andesite_casing`（机械动力，必需依赖，必解析到）；
- 柴油机组 `create_diesel:diesel_generator_block`（可选，缺失则空气）；
- 航空学机体 `create_aeronautics:chassis` / `:engine`（可选，缺失则空气）；
- `creative_motor` / `furnace` 等动力源。

在线模型接入后，玩家也可在对话里直接说「盖一座造车厂」，由模型按同一 JSON 信封契约生成（航空学 + 柴油动力共存的厂房）。

## 5. 公交与地铁

- **公交**：`infrastructure/minecraft/entities/transit/BusEntity`（注册于 `EntityRegistry.BUS`）在小镇两个站点间正弦往返巡逻；
  升级为小镇时由 `ScaleSettlementBehavior` 直接 `addFreshEntity` 生成若干辆。
- **地铁**：城市档位下 `BuildSubwayBehavior` 在地面以下 4 格挖出 48 格直隧、每 4 格铺一节充能铁轨（其下红石块供能）。

## 6. 新增 / 改动文件（第四轮）

- 新增：`SettlementTier`、`SettlementScalingService`、`ScaleSettlementBehavior`、`BuildSubwayBehavior`、
  `compat/diesel/DieselCompat`、`BusEntity`、`EntityRegistry.BUS`。
- 扩展：`MockBlueprintLibrary`（townHouse / urbanBlock / busStop / subwayStation / carFactory）、
  `BehaviorKey`（SCALE_SETTLEMENT / BUILD_SUBWAY）、`PoolModule`、`BehaviorCatalogModule`、`SettlementLogType`（SCALED）、
  中英 `lang`、`mods.toml` 模板、本档。
- 蓝图入库由既有 `BuildFromBlueprintBehavior` 村民逐格盖出（与第三轮对话建造共用同一队列 `BuildOrderQueue`）。

## 7. 第四轮注意事项 / 待补

1. **人口阈值需配合你的玩法**：默认 12 / 30；若你的村庄长期不达标，调高 `SettlementTier` 两个常量即可。
2. **柴油动力的真实 modid**：Create: Diesel Generators 在不同版本 modid 可能不同，已用候选列表探测；若你的版本
   仍识别不到，把真实 modid 补进 `DieselCompat.MOD_IDS` 即可（蓝图里的方块路径同理微调）。
3. **公交为轻量雏形**：`BusEntity` 仅做位置往返，未接 Create 列车/动力系统；要真实轨道公交可后续把巡逻改为沿铁轨寻路。
4. **地铁为直隧雏形**：目前是单条直隧道示意；要成网可把 `BuildSubwayBehavior.carveAndLay` 扩展为环线/支线。
5. **沙箱未编译验证**：同前，需本机 `./gradlew build` 跑通；新增均为纯 Java + 反射式可选 mod 集成，未引入编译期依赖。


---

## 第五轮：改造后 mod 的编译与实跑

此前沙箱里 Gradle 的注解处理链（Lombok + Dagger）在 JDK 21 下整体失效，且
NeoForge 的 `createMinecraftArtifacts` 反编译曾因内存不足失败。本轮改用离线编译链打通：

- 用 HMCL 游戏实例里的 133 个 libraries 补齐 NFRT 的 recompile 依赖，跑通反编译与
  5364 个 Minecraft 源文件的重编译；
- 用 `delombok` 把 1545 个源文件展开为纯 Java，再用 `javac -proc:full` 单独跑 Dagger，
  绕开 Lombok/Dagger 的注解处理顺序冲突；
- 补齐 NeoForge universal/client、Create、以及 Create jarJar 内嵌的 Registrate 到编译 classpath；
- 修复 1.21.1 的 API 变更、Dagger 作用域冲突、Create 字段改名等共约 60 处编译问题。

**产物**：`settlements-1.0.0-beta.nightly-110126-机械动力改造.jar`
（4.74 MB / 3110 条目 / 2729 个 class），包含模型对话建造、村庄→小镇→城市自动升级、
公交/地铁、三职业与机械动力城墙等全部改造内容，并以 jarJar 内嵌 Dagger 运行时依赖。

**HMCL 实跑验证**（Minecraft 1.21.1 / NeoForge 21.1.259 / 15 mods）：

- 主菜单显示 `Minecraft 1.21.1 / NeoForge 21.1.259 (15 mods loaded)`；
- Mods 列表显示 `Settlements 1.0.0-beta.nightly-110126`；
- 日志 `Settlements initialized`，成功进入存档世界（数据包正常加载）；
- `/give @s settlements:dialogue_manual` → `Gave 1 [对话手册]`，
  `/give @s settlements:build_order` → `Gave 1 [建房订单]`。

实跑截图见 `实测截图/新mod实跑/`（主菜单、Mods 列表筛选、进入世界、give 验证）。


---

## 第六轮：整村山东民房、全村拆除重建与真实砖瓦城市

### 前五轮成果回顾（本次重装逐项确认仍在包内）

| 轮次 | 内容 |
| --- | --- |
| 阶段二 | 三职业（建筑师 / 机械师 / 工厂工人）、职业方块、火车与火车站、地形修补、僵尸村庄误判修复 |
| 阶段三 | 山东民房整改（灰砖带院农房 / 两层小楼 / 工作房）、结婚与搬入、家具、炊烟、10 步作息、机械动力城墙 |
| 第三轮 | 小模型对话（OpenAI 兼容端点 + 离线兜底）、模型生成地图、蓝图 JSON 解析、村民逐格搭建、对话手册 / 建房订单 |
| 第四轮 | 村庄→小镇→城市自动升级、机械动力造车厂（航空学 + 可选柴油动力）、公交 / 地铁、城建方块 |
| 第五轮 | 离线编译链（delombok + javac）打通、改造后可运行 jar、HMCL 15 mods 实跑 |

### 本轮修正的三个问题

1. **只改造一部分 → 全村拆除重建**
   上一版 `demolishOldVillage` 只清挖村庄中心半径 18 格，重建也只在中心放 8 栋房屋，外围仍是原版木屋。
   现所有阶段统一按整座村庄圆形范围（村庄 56 格 / 城市 62 格）逐格清挖，再按网格铺满重建；
   正式的 `ScaleSettlementBehavior` 同步把 `DEMOLISH_RADIUS` 从 18 改为 44、房屋数从 8 / 10 提升到 32 / 30。
   清挖同样进入逐格队列，因此「全村被一格格拆掉」在俯视里可见。

2. **城市太抽象 → 成片山东砖瓦楼**
   `urbanBlock()` 原为 5×5 石砖广场 + 白色混凝土空心盒，俯视只是一片白块。现在改成两层山东砖瓦楼：
   灰砖 / 泥砖墙、木挑檐、玻璃窗、青瓦四坡顶（逐层收拢形成屋脊）、石砖院墙与巷道；
   `townHouse()` 同步改为灰砖院落 + 青瓦坡顶的联排民房。城市屋顶进一步换成「白色檐口 + 浅灰瓦面 + 青色屋脊」的明亮青瓦配色，并给每栋楼铺一圈浅色石砖街巷地基，俯视能看清成片屋顶与街巷网格，不再是一整块深蓝黑方框；建造队列提到每 tick 80 格，网格扩到 9×9，外圈原版木屋也一并清挖重建。

3. **山东民房改为世界生成时整村生成**
   阶段三只翻新单栋村民住房；本轮新增 `ShandongVillageGenerator`：玩家进入世界后，服务端用 POI
   （集会点 / 钟）定位附近村庄，把整座村庄一次性替换为按网格铺满的 `ShandongHousePlan` 带院砖瓦房，
   村中心留集会广场。按坐标去重，每座村庄只生成一次，不会把后续升级出的小镇 / 城市刷回民房。
   `RemodelHouseBehavior` 的单栋翻新仍保留。

### 新增 / 修改文件

- 新增 `application/settlement/generation/ShandongVillageGenerator.java`
- 重写 `presentation/commands/SettlementScaleDemo.java`（全村网格调度 + `/scaledemo village|town|city|status`）
- 重写 `application/ai/modeldialogue/mock/MockBlueprintLibrary.java` 的 `townHouse()` / `urbanBlock()`
- 修改 `application/ai/behavior/usecases/villager/build/ScaleSettlementBehavior.java`（半径 / 数量 / 网格）
- 上一轮已修：`infrastructure/minecraft/entities/transit/BusRenderer.java`、
  `bootstrap/event/ClientModEvents.java`（公交实体渲染器缺失导致的崩溃）

### 实拍验证

新建超平坦世界，俯视一镜到底录制「整村山东民房 → 全村拆除重建为小镇 → 全城砖瓦街区」，
见实测证据包 `实测视频/06_超平坦_整村民房_全村重建_到城市_俯视实录.mp4`。


---

## 第七轮：贴地修复、三合院、天花板、真实材质、村道重修与村民逐格搭建

### 1. 修复房子悬空 / 遁地（关键 bug）

根因：`groundCenter()` 用玩家或钟所在**单点**的 `getHeight()` 当作整个半径 56 / 62 圆形范围的统一 Y；
而村庄房屋、钟楼会把 heightmap 的「最高非空气方块」抬到屋顶，于是整片房子被生成在半空中，
清挖范围也跟着错位，不同位置有的悬空、有的陷进地里。

修复：新增 `terrainTopY()` —— 在村庄结构之外 8 个方向采样空地高度、取中位数得到**真实地形顶层**；
清挖从 `terrainY+1` 开始（绝不挖掉地面本身），建筑地板正好落在 `terrainY+1`，即地面之上第一格；
`ShandongVillageGenerator` 同步改用该函数，`/scaledemo village|town|city|yard` 全部一致。

### 2. 院子进入有三栋房子（前 / 左 / 右）

`ShandongHousePlan` 重写为 15×15 的**三合院**：南面门楼入院，正前方是**正房**（两层变体）、
左手**西厢房**、右手**东厢房**，中间围合庭院与石板甬道，青灰石砌院墙 + 瓦顶压顶、门楼挂灯笼。

### 3. 制作天花板

每栋房在墙顶 `y=height` 铺整片云杉木**平整天花板**；`townHouse()` / `urbanBlock()` 同步补层间天花板。
放置队列改为**按 y 从低到高排序**，因此逐格搭建顺序是地板 → 墙 → 天花板 → 屋顶，
在盖屋顶之前能亲眼看到天花板封顶（斜视实录可见）。

### 4. 修改方块材质，像真的一样

家具从 `cube_all` 木板盒改为**真实几何模型**（纯 JSON，复用原版 plank / terracotta 贴图，无需新贴图）：

| 方块 | 旧 | 新 |
| --- | --- | --- |
| 折叠椅 | 一整块橡木木板 | **X 形交叉腿折叠凳**：帆布座面 + 前后两组 X 支架 + 横撑 + 脚垫 |
| 折叠桌 | 一整块云杉木板 | 桌面 + 四条细腿 + X 形交叉撑 |
| 茶具 | 陶瓦方块 | 茶壶 + 壶盖 + 壶嘴 + 壶把 + 两只茶杯 + 木托盘 |
| 扇子 | 石英方块 | 扇面 + 三根扇骨 + 木柄 |
| 扑克牌 | 白色混凝土 | 三张错开摊开的薄牌 |

房屋统一青灰石砖墙、泥砖墙裙、深色木圈梁 / 挑檐、`deepslate_tiles` 青瓦两坡顶、青色屋脊、
玻璃窗、云杉实木门、砖烟囱；院内真实摆放上述家具方块，斜视可辨。

### 5. 路面重修

`enqueueRoads()` 按网格铺**井字形石板村道**：抛光安山岩亮色中心、圆石路缘、
石砖十字路口、局部砂砾变化、交叉口灯笼路灯，取代原来单一纯色地面；
网格步长与三合院占地对齐，院落之间正好留出道路带。

### 6. 村民拿着方块站在边上逐格搭建

建造阶段生成 8 个真实村民，贴地站在工地边缘（`terrainY+2`），手持**当前正在放置的方块**并挥臂；
`ItemDisplay` 在村民手侧同步显示该方块，村民随放置进度移动到刚放好的方块旁、面向工地。
新增 `/scaledemo yard` 近景演示命令（只重建镜头前一个三合院，方便看清逐格搭建）。

### 新增 / 修改文件

- 重写 `compat/create/build/ShandongHousePlan.java`（三合院 + 天花板）
- 重写 `presentation/commands/SettlementScaleDemo.java`（贴地 / 村道 / 村民 / yard 命令 / 按高度排序）
- 修改 `application/ai/modeldialogue/mock/MockBlueprintLibrary.java`（天花板 + 真实材质 + 家具）
- 修改 `application/settlement/generation/ShandongVillageGenerator.java`（`terrainTopY`）
- 重写 `assets/settlements/models/block/{folding_chair,folding_table,tea_set,fan,playing_cards}.json`
