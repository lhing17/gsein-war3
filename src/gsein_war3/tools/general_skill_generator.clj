(ns gsein-war3.tools.general-skill-generator)

;; 通魔技能生成器
(def orders
  ["AImove" "absorb" "acidbomb" "acolyteharvest" "ambush" "ancestralspirit"
   "ancestralspirittarget" "animatedead" "antimagicshell" "attack" "attackground" "attackonce"
   "attributemodskill" "auraunholy" "auravampiric" "autodispel" "autodispeloff" "autodispelon"
   "autoentangle" "autoentangleinstant" "autoharvestgold" "autoharvestlumber" "avatar" "avengerform"
   "awaken" "banish" "barkskin" "barkskinoff" "barkskinon" "battleroar"
   "battlestations" "bearform" "berserk" "blackarrow" "blackarrowoff" "blackarrowon"
   "blight" "blink" "blizzard" "bloodlust" "bloodlustoff" "bloodluston"
   "board" "breathoffire" "breathoffrost" "build" "burrow" "cannibalize"
   "carrionscarabs" "carrionscarabsinstant" "carrionscarabsoff" "carrionscarabson" "carrionswarm" "chainlightning"
   "channel" "charm" "chemicalrage" "cloudoffog" "clusterrockets" "coldarrows"
   "coldarrowstarg" "controlmagic" "corporealform" "corrosivebreath" "coupleinstant" "coupletarget"
   "creepanimatedead" "creepdevour" "creepheal" "creephealoff" "creephealon" "creepthunderbolt"
   "creepthunderclap" "cripple" "curse" "curseoff" "curseon" "cyclone"
   "darkconversion" "darkportal" "darkritual" "darksummoning" "deathanddecay" "deathcoil"
   "deathpact" "decouple" "defend" "detectaoe" "detonate" "devour"
   "devourmagic" "disassociate" "disenchant" "dismount" "dispel" "divineshield"
   "doom" "drain" "dreadlordinferno" "dropitem" "drunkenhaze" "earthquake"
   "eattree" "elementalfury" "ensnare" "ensnareoff" "ensnareon" "entangle"
   "entangleinstant" "entanglingroots" "etherealform" "evileye" "faeriefire" "faeriefireoff"
   "faeriefireon" "fanofknives" "farsight" "fingerofdeath" "firebolt" "flamestrike"
   "flamingarrows" "flamingarrowstarg" "flamingattack" "flamingattacktarg" "flare" "forceboard"
   "forceofnature" "forkedlightning" "freezingbreath" "frenzy" "frenzyoff" "frenzyon"
   "frostarmor" "frostarmoroff" "frostarmoron" "frostnova" "getitem" "gold2lumber"
   "grabtree" "harvest" "heal" "healingspray" "healingward" "healingwave"
   "healoff" "healon" "hex" "holdposition" "holybolt" "howlofterror"
   "humanbuild" "immolation" "impale" "incineratearrow" "incineratearrowoff" "incineratearrowon"
   "inferno" "innerfire" "innerfireoff" "innerfireon" "instant" "invisibility"
   "lavamonster" "lightningshield" "load" "loadarcher" "loadcorpse" "loadcorpseinstant"
   "locustswarm" "lumber2gold" "magicdefense" "magicleash" "magicundefense" "manaburn"
   "manaflareoff" "manaflareon" "manashieldoff" "manashieldon" "massteleport" "mechanicalcritter"
   "metamorphosis" "militia" "militiaconvert" "militiaoff" "militiaunconvert" "mindrot"
   "mirrorimage" "monsoon" "mount" "mounthippogryph" "move" "nagabuild"
   "neutraldetectaoe" "neutralinteract" "neutralspell" "nightelfbuild" "orcbuild" "parasite"
   "parasiteoff" "parasiteon" "patrol" "phaseshift" "phaseshiftinstant" "phaseshiftoff"
   "phaseshifton" "phoenixfire" "phoenixmorph" "poisonarrows" "poisonarrowstarg" "polymorph"
   "possession" "preservation" "purge" "rainofchaos" "rainoffire" "raisedead"
   "raisedeadoff" "raisedeadon" "ravenform" "recharge" "rechargeoff" "rechargeon"
   "rejuvination" "renew" "renewoff" "renewon" "repair" "repairoff"
   "repairon" "replenish" "replenishlife" "replenishlifeoff" "replenishlifeon" "replenishmana"
   "replenishmanaoff" "replenishmanaon" "replenishoff" "replenishon" "request_hero" "requestsacrifice"
   "restoration" "restorationoff" "restorationon" "resumebuild" "resumeharvesting" "resurrection"
   "returnresources" "revenge" "revive" "roar" "robogoblin" "root"
   "sacrifice" "sanctuary" "scout" "selfdestruct" "selfdestructoff" "selfdestructon"
   "sentinel" "setrally" "shadowsight" "shadowstrike" "shockwave" "silence"
   "sleep" "slow" "slowoff" "slowon" "smart" "soulburn"
   "soulpreservation" "spellshield" "spellshieldaoe" "spellsteal" "spellstealoff" "spellstealon"
   "spies" "spiritlink" "spiritofvengeance" "spirittroll" "spiritwolf" "stampede"
   "standdown" "starfall" "stasistrap" "steal" "stomp" "stoneform"
   "stop" "submerge" "summonfactory" "summongrizzly" "summonphoenix" "summonquillbeast"
   "summonwareagle" "tankdroppilot" "tankloadpilot" "tankpilot" "taunt" "thunderbolt"
   "thunderclap" "tornado" "townbelloff" "townbellon" "tranquility" "transmute"
   "unavatar" "unavengerform" "unbearform" "unburrow" "uncoldarrows" "uncorporealform"
   "undeadbuild" "undefend" "undivineshield" "unetherealform" "unflamingarrows" "unflamingattack"
   "unholyfrenzy" "unimmolation" "unload" "unloadall" "unloadallcorpses" "unloadallinstant"
   "unpoisonarrows" "unravenform" "unrobogoblin" "unroot" "unstableconcoction" "unstoneform"
   "unsubmerge" "unsummon" "unwindwalk" "vengeance" "vengeanceinstant" "vengeanceoff"
   "vengeanceon" "volcano" "voodoo" "ward" "waterelemental" "wateryminion"
   "web" "weboff" "webon" "whirlwind" "windwalk" "wispharvest"])

;; 多个值的属性
(def multi-val-attr [:data-a :data-b :data-c :data-d :data-e :order
                     :tip :ubertip :cool :cost :rng :area])

;; 目标类型常量 0-无目标 1-单位目标 2-点目标 3-单位或点目标
(def target-type-const [0 1 2 3])

;; 通魔选项 多选 1-图标可见 2-目标选取图像 4-物理魔法 8-通用魔法 16-单独施放
;; 物理魔法：不能作用于魔免，不能作用于虚无，自身虚无时不可使用。
;; 通用魔法：能作用于魔免，能作用于虚无，自身虚无时可使用，技能最大等级为1或耗魔为0时无视沉默魔法。
;; 物理+通用魔法：能作用于魔免，不能作用于虚无，自身虚无时不可使用。
(def ability-flag-const [1 2 4 8 16])

(def default-values {:data-a 0 :data-b 0 :data-c 0 :data-d 0 :data-e 0
                     :tip "" :ubertip "" :cool 0 :cost 0 :rng 0 :area 0
                     :art "" :x 0 :y 0 :name "通魔" :hotkey "A" :level 1
                     :race "orc" :rng 500})


(comment

  )