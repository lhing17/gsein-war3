<template>
  <div class="page">
    <h2>通魔技能生成器</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form label-width="140px" class="form">
          <el-form-item label="ID">
            <el-input v-model="form.id" placeholder="如 ANcl" />
          </el-form-item>
          <el-form-item label="名字">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="热键">
            <el-input v-model="form.hotkey" style="width: 80px" />
          </el-form-item>
          <el-form-item label="种族">
            <el-select v-model="form.race">
              <el-option label="人类 (human)" value="human" />
              <el-option label="兽人 (orc)" value="orc" />
              <el-option label="亡灵 (undead)" value="undead" />
              <el-option label="暗夜 (nightelf)" value="nightelf" />
            </el-select>
          </el-form-item>
          <el-form-item label="等级">
            <el-input-number v-model="form.levels" :min="1" :max="10" />
          </el-form-item>
          <el-form-item label="图标路径">
            <el-input v-model="form.art" placeholder="如 ReplaceableTextures\\CommandButtons\\BTNxxx.blp" />
          </el-form-item>
          <el-form-item label="按钮位置 X">
            <el-input-number v-model="form.x" :min="0" :max="3" />
          </el-form-item>
          <el-form-item label="按钮位置 Y">
            <el-input-number v-model="form.y" :min="0" :max="2" />
          </el-form-item>
          <el-form-item label="冷却时间">
            <el-input-number v-model="form.cool" :min="0" />
          </el-form-item>
          <el-form-item label="魔法消耗">
            <el-input-number v-model="form.cost" :min="0" />
          </el-form-item>
          <el-form-item label="影响区域">
            <el-input-number v-model="form.area" :min="0" />
          </el-form-item>
          <el-form-item label="施法距离">
            <el-input-number v-model="form.rng" :min="0" />
          </el-form-item>
          <el-form-item label="DataA">
            <el-input-number v-model="form['data-a']" />
          </el-form-item>
          <el-form-item label="DataB (目标类型)">
            <el-select v-model="form['data-b']">
              <el-option label="0 - 无目标" :value="0" />
              <el-option label="1 - 单位目标" :value="1" />
              <el-option label="2 - 点目标" :value="2" />
              <el-option label="3 - 单位或点目标" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="DataC (选项)">
            <el-input-number v-model="form['data-c']" />
          </el-form-item>
          <el-form-item label="DataD">
            <el-input-number v-model="form['data-d']" />
          </el-form-item>
          <el-form-item label="DataE">
            <el-input-number v-model="form['data-e']" />
          </el-form-item>
          <el-form-item label="基础命令 ID">
            <el-select v-model="form.order" filterable clearable>
              <el-option v-for="o in orders" :key="o" :label="o" :value="o" />
            </el-select>
          </el-form-item>
          <el-form-item label="Tip">
            <el-input v-model="form.tip" type="textarea" :rows="2" />
          </el-form-item>
          <el-form-item label="Ubertip">
            <el-input v-model="form.ubertip" type="textarea" :rows="4" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="render">生成预览</el-button>
            <el-button @click="copyResult">复制结果</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>预览</template>
          <pre class="preview">{{ result }}</pre>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'

const orders = [
  "AImove","absorb","acidbomb","acolyteharvest","ambush","ancestralspirit",
  "ancestralspirittarget","animatedead","antimagicshell","attack","attackground","attackonce",
  "attributemodskill","auraunholy","auravampiric","autodispel","autodispeloff","autodispelon",
  "autoentangle","autoentangleinstant","autoharvestgold","autoharvestlumber","avatar","avengerform",
  "awaken","banish","barkskin","barkskinoff","barkskinon","battleroar",
  "battlestations","bearform","berserk","blackarrow","blackarrowoff","blackarrowon",
  "blight","blink","blizzard","bloodlust","bloodlustoff","bloodluston",
  "board","breathoffire","breathoffrost","build","burrow","cannibalize",
  "carrionscarabs","carrionscarabsinstant","carrionscarabsoff","carrionscarabson","carrionswarm","chainlightning",
  "channel","charm","chemicalrage","cloudoffog","clusterrockets","coldarrows",
  "coldarrowstarg","controlmagic","corporealform","corrosivebreath","coupleinstant","coupletarget",
  "creepanimatedead","creepdevour","creepheal","creephealoff","creephealon","creepthunderbolt",
  "creepthunderclap","cripple","curse","curseoff","curseon","cyclone",
  "darkconversion","darkportal","darkritual","darksummoning","deathanddecay","deathcoil",
  "deathpact","decouple","defend","detectaoe","detonate","devour",
  "devourmagic","disassociate","disenchant","dismount","dispel","divineshield",
  "doom","drain","dreadlordinferno","dropitem","drunkenhaze","earthquake",
  "eattree","elementalfury","ensnare","ensnareoff","ensnareon","entangle",
  "entangleinstant","entanglingroots","etherealform","evileye","faeriefire","faeriefireoff",
  "faeriefireon","fanofknives","farsight","fingerofdeath","firebolt","flamestrike",
  "flamingarrows","flamingarrowstarg","flamingattack","flamingattacktarg","flare","forceboard",
  "forceofnature","forkedlightning","freezingbreath","frenzy","frenzyoff","frenzyon",
  "frostarmor","frostarmoroff","frostarmoron","frostnova","getitem","gold2lumber",
  "grabtree","harvest","heal","healingspray","healingward","healingwave",
  "healoff","healon","hex","holdposition","holybolt","howlofterror",
  "humanbuild","immolation","impale","incineratearrow","incineratearrowoff","incineratearrowon",
  "inferno","innerfire","innerfireoff","innerfireon","instant","invisibility",
  "lavamonster","lightningshield","load","loadarcher","loadcorpse","loadcorpseinstant",
  "locustswarm","lumber2gold","magicdefense","magicleash","magicundefense","manaburn",
  "manaflareoff","manaflareon","manashieldoff","manashieldon","massteleport","mechanicalcritter",
  "metamorphosis","militia","militiaconvert","militiaoff","militiaunconvert","mindrot",
  "mirrorimage","monsoon","mount","mounthippogryph","move","nagabuild",
  "neutraldetectaoe","neutralinteract","neutralspell","nightelfbuild","orcbuild","parasite",
  "parasiteoff","parasiteon","patrol","phaseshift","phaseshiftinstant","phaseshiftoff",
  "phaseshifton","phoenixfire","phoenixmorph","poisonarrows","poisonarrowstarg","polymorph",
  "possession","preservation","purge","rainofchaos","rainoffire","raisedead",
  "raisedeadoff","raisedeadon","ravenform","recharge","rechargeoff","rechargeon",
  "rejuvination","renew","renewoff","renewon","repair","repairoff",
  "repairon","replenish","replenishlife","replenishlifeoff","replenishlifeon","replenishmana",
  "replenishmanaoff","replenishmanaon","replenishoff","replenishon","request_hero","requestsacrifice",
  "restoration","restorationoff","restorationon","resumebuild","resumeharvesting","resurrection",
  "returnresources","revenge","revive","roar","robogoblin","root",
  "sacrifice","sanctuary","scout","selfdestruct","selfdestructoff","selfdestructon",
  "sentinel","setrally","shadowsight","shadowstrike","shockwave","silence",
  "sleep","slow","slowoff","slowon","smart","soulburn",
  "soulpreservation","spellshield","spellshieldaoe","spellsteal","spellstealoff","spellstealon",
  "spies","spiritlink","spiritofvengeance","spirittroll","spiritwolf","stampede",
  "standdown","starfall","stasistrap","steal","stomp","stoneform",
  "stop","submerge","summonfactory","summongrizzly","summonphoenix","summonquillbeast",
  "summonwareagle","tankdroppilot","tankloadpilot","tankpilot","taunt","thunderbolt",
  "thunderclap","tornado","townbelloff","townbellon","tranquility","transmute",
  "unavatar","unavengerform","unbearform","unburrow","uncoldarrows","uncorporealform",
  "undeadbuild","undefend","undivineshield","unetherealform","unflamingarrows","unflamingattack",
  "unholyfrenzy","unimmolation","unload","unloadall","unloadallcorpses","unloadallinstant",
  "unpoisonarrows","unravenform","unrobogoblin","unroot","unstableconcoction","unstoneform",
  "unsubmerge","unsummon","unwindwalk","vengeance","vengeanceinstant","vengeanceoff",
  "vengeanceon","volcano","voodoo","ward","waterelemental","wateryminion",
  "web","weboff","webon","whirlwind","windwalk","wispharvest"
]

const form = reactive({
  id: 'ANcl',
  name: '通魔',
  hotkey: 'A',
  race: 'orc',
  levels: 1,
  art: '',
  x: 0,
  y: 0,
  cool: 0,
  cost: 0,
  area: 0,
  rng: 500,
  'data-a': 0,
  'data-b': 0,
  'data-c': 0,
  'data-d': 0,
  'data-e': 0,
  order: 'channel',
  tip: '',
  ubertip: '',
})

const loading = ref(false)
const result = ref('')

function buildData() {
  return {
    id: form.id,
    name: form.name,
    hotkey: form.hotkey,
    race: form.race,
    levels: form.levels,
    art: form.art,
    x: form.x,
    y: form.y,
    cool: form.cool,
    cost: form.cost,
    area: form.area,
    rng: form.rng,
    'data-a': form['data-a'],
    'data-b': form['data-b'],
    'data-c': form['data-c'],
    'data-d': form['data-d'],
    'data-e': form['data-e'],
    order: form.order,
    tip: form.tip,
    ubertip: form.ubertip,
  }
}

async function render() {
  loading.value = true
  try {
    const data = buildData()
    const res = await callClojure('general-skill-render', ['-d', JSON.stringify(data)])
    if (res.success) {
      const parsed = eval(res.stdout)
      result.value = parsed.output || ''
    } else {
      ElMessage.error(res.stderr || '生成失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '请求失败')
  } finally {
    loading.value = false
  }
}

function copyResult() {
  if (!result.value) return
  navigator.clipboard.writeText(result.value)
  ElMessage.success('已复制到剪贴板')
}
</script>

<style scoped>
.form {
  max-width: 600px;
}
.preview {
  white-space: pre-wrap;
  word-break: break-all;
  font-family: monospace;
  font-size: 12px;
}
</style>
