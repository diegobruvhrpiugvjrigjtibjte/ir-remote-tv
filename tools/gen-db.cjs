// Genera app/src/main/assets/tvdb.json dal database pubblico Flipper-IRDB (cartella TVs): node tools/gen-db.cjs <cartella TVs>
const fs = require('fs'), path = require('path');
const root = process.argv[2]; const out = path.join(__dirname, '..', 'app', 'src', 'main', 'assets', 'tvdb.json');
const SUPPORTED = new Set(['NEC', 'NECext', 'NEC42', 'NEC42ext', 'Samsung32', 'RC5', 'RC5X', 'RC6', 'SIRC', 'SIRC15', 'SIRC20']);
const DIGITS = { zero: 0, one: 1, two: 2, three: 3, four: 4, five: 5, six: 6, seven: 7, eight: 8, nine: 9 };

function mapKey(raw) {
  const n = String(raw).toLowerCase().replace(/[^a-z0-9+]+/g, '_').replace(/^_+|_+$/g, '');
  let m;
  if (/^(power|pwr|on_off|onoff|standby|power_toggle|power_on_off|tv_power|off_on|tv_on_off|power_onoff|power_key)$/.test(n)) return 'POWER';
  if (/mute/.test(n)) return 'MUTE';
  if (/^(vol(ume)?_?(up|plus|inc|\+)|v_?up|vol\+|volume_?\+|vol_incr|nav_vol_up|vol_u)$/.test(n)) return 'VOL_UP';
  if (/^(vol(ume)?_?(dn|down|minus|dec|-)|v_?down|vol_-|vol_d|vol_decr|nav_vol_down|vol_dwn)$/.test(n)) return 'VOL_DOWN';
  if (/^(ch(annel|an)?_?(up|plus|next|inc|\+)|prog(ram)?_?(up|plus|next|\+)|ch\+|channel_?\+|p_?up|chan_up|ch_u)$/.test(n)) return 'CH_UP';
  if (/^(ch(annel|an)?_?(dn|down|prev|previous|minus|dec|-)|prog(ram)?_?(dn|down|prev|minus)|p_?down|chan_down|ch_d|ch_dwn)$/.test(n)) return 'CH_DOWN';
  if (/^(up|nav_up|arrow_up|cursor_up|dpad_up|k_up|up_arrow|navigation_up)$/.test(n)) return 'UP';
  if (/^(down|nav_dn|nav_down|arrow_down|cursor_down|dpad_down|k_down|down_arrow|navigation_down|dn)$/.test(n)) return 'DOWN';
  if (/^(left|nav_lft|nav_left|arrow_left|cursor_left|dpad_left|k_left|left_arrow|navigation_left)$/.test(n)) return 'LEFT';
  if (/^(right|nav_rgt|nav_right|arrow_right|cursor_right|dpad_right|k_right|right_arrow|navigation_right)$/.test(n)) return 'RIGHT';
  if (/^(ok|enter|select|nav_ok|center|ok_enter|k_ok|set|confirm|ok_select|dpad_center)$/.test(n)) return 'OK';
  if (/^(back|return|nav_back|previous_screen|k_back|go_back|return_back)$/.test(n)) return 'BACK';
  if (/^(menu|nav_menu|main_menu|k_menu|menu_tv|tv_menu)$/.test(n)) return 'MENU';
  if (/^(home|smart|smart_hub|hub|vidaa|nav_home|launcher|hismart|k_home|smart_tv|smarthub|my_home)$/.test(n)) return 'HOME';
  if (/^(input|source|tv_av|av|input_source|tv_input|nav_input|inputs|source_list|tv_video|ext|extinput|hdmi|k_source|k_input)$/.test(n)) return 'INPUT';
  if (/^(exit|nav_exit|close|k_exit)$/.test(n)) return 'EXIT';
  if (/^(info|display|information|k_info|nav_info)$/.test(n)) return 'INFO';
  if (/^(guide|epg|tv_guide|program_guide|nav_guide|k_guide)$/.test(n)) return 'GUIDE';
  if (/^(settings?|setup|nav_setting|tools|options?|k_setup|quick_menu|q_menu|nav_settings)$/.test(n)) return 'SETTINGS';
  if (/^(play|add_play|play_pause_play)$/.test(n)) return 'PLAY';
  if (/^(pause|add_pause|play_pause|play_pause_toggle)$/.test(n)) return 'PAUSE';
  if (/^(stop|add_stop)$/.test(n)) return 'STOP';
  if (/^(rew|rewind|fast_ba|fast_back|add_rew|rw|fr|back_fast|fastback|fast_rewind)$/.test(n)) return 'REW';
  if (/^(ffwd|ff|forward|fast_fo|fast_forward|add_ffd|fastforward|fast_fwd|fwd)$/.test(n)) return 'FFWD';
  if (/^(red|red_a|k_red|color_red)$/.test(n)) return 'RED';
  if (/^(green|green_b|k_green|color_green)$/.test(n)) return 'GREEN';
  if (/^(yellow|yellow_c|k_yellow|color_yellow)$/.test(n)) return 'YELLOW';
  if (/^(blue|blue_d|k_blue|color_blue)$/.test(n)) return 'BLUE';
  if (/youtube/.test(n)) return 'YOUTUBE';
  if (/netflix/.test(n)) return 'NETFLIX';
  if (/prime|amazon/.test(n)) return 'PRIME';
  if (/disney/.test(n)) return 'DISNEY';
  if (/spotify/.test(n)) return 'SPOTIFY';
  if (/^(apps?|app_store|applications?|app_list|app_menu)$/.test(n)) return 'APPS';
  if (/subtitle|^(cc|sub|sub_t|subt)$/.test(n)) return 'SUBTITLE';
  if (/sleep/.test(n)) return 'SLEEP';
  if (/^(txt|text|teletext|ttx)$/.test(n)) return 'TEXT';
  if (/^(ch_list|chlist|channel_list|list|favorites?|fav)$/.test(n)) return 'CHLIST';
  if (/^(last|recall|prev|previous|flashback|prev_ch|return_ch)$/.test(n)) return 'LAST';
  if ((m = /^(?:num_|digit_|key_|number_|n|d|k_|button_)?([0-9])$/.exec(n))) return 'D' + m[1];
  if (n in DIGITS) return 'D' + DIGITS[n];
  return null;
}

const le = (bytes) => bytes.slice(0, 4).reduce((a, b, i) => a + b * Math.pow(256, i), 0);
function parseFile(txt) {
  const out = []; for (const blk of txt.split(/^#.*$/m)) {
    const f = {}; for (const ln of blk.split('\n')) { const i = ln.indexOf(':'); if (i > 0) f[ln.slice(0, i).trim()] = ln.slice(i + 1).trim(); }
    if (f.name && f.type) out.push(f);
  } return out;
}
function convert(f) {
  if (f.type === 'parsed') {
    if (!SUPPORTED.has(f.protocol)) return null;
    const a = (f.address || '').split(/\s+/).map((x) => parseInt(x, 16)), c = (f.command || '').split(/\s+/).map((x) => parseInt(x, 16));
    if (a.some(isNaN) || c.some(isNaN)) return null; return { p: f.protocol, a: le(a), c: le(c) };
  }
  if (f.type === 'raw') {
    const d = (f.data || '').split(/\s+/).map(Number).filter((x) => x > 0); const fr = parseInt(f.frequency, 10) || 38000;
    if (d.length < 4 || d.length > 400 || d.some(isNaN)) return null; return { r: d, f: fr };
  }
  return null;
}

const brands = [];
for (const b of fs.readdirSync(root, { withFileTypes: true }).filter((e) => e.isDirectory())) {
  const remotes = [];
  for (const file of fs.readdirSync(path.join(root, b.name)).filter((x) => x.endsWith('.ir'))) {
    const keys = {}; let n = 0;
    for (const f of parseFile(fs.readFileSync(path.join(root, b.name, file), 'utf8'))) {
      const k = mapKey(f.name); if (!k || keys[k]) continue; const c = convert(f); if (!c) continue; keys[k] = c; n++;
    }
    if (n >= 4 && (keys.POWER || keys.VOL_UP)) remotes.push({ n: file.replace(/\.ir$/, '').replace(/_/g, ' '), b: keys, count: n });
  }
  // senza doppioni identici, i piu' completi per primi
  remotes.sort((x, y) => y.count - x.count); const seen = new Set(), uniq = [];
  for (const r of remotes) { const sig = JSON.stringify(r.b); if (seen.has(sig)) continue; seen.add(sig); uniq.push({ n: r.n, b: r.b }); }
  if (uniq.length) brands.push({ n: b.name, r: uniq });
}
const FIRST = ['Samsung', 'LG', 'Sony', 'Hisense', 'TCL', 'Philips', 'Xiaomi', 'Panasonic', 'Sharp', 'Toshiba', 'Vizio', 'Roku', 'Thomson', 'Grundig', 'JVC', 'Haier', 'Sanyo', 'Hitachi', 'Insignia', 'Android'];
brands.sort((a, b) => { const ia = FIRST.indexOf(a.n), ib = FIRST.indexOf(b.n); return (ia < 0 ? 999 : ia) - (ib < 0 ? 999 : ib) || a.n.localeCompare(b.n); });
fs.mkdirSync(path.dirname(out), { recursive: true }); fs.writeFileSync(out, JSON.stringify({ v: 1, brands }));
const keyCount = {}; let remotesN = 0; for (const b of brands) for (const r of b.r) { remotesN++; for (const k of Object.keys(r.b)) keyCount[k] = (keyCount[k] || 0) + 1; }
console.log('marche:', brands.length, 'telecomandi:', remotesN, 'dimensione:', (fs.statSync(out).size / 1024).toFixed(0) + ' KB'); console.log(JSON.stringify(keyCount));
