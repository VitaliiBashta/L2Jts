package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class detect_party_wizard extends Mystic {
    private static final int duelist = 30;
    private static final int dreadnought = 42;
    private static final int phoenix_knight = 24;
    private static final int hell_knight = 27;
    private static final int sagittarius = 23;
    private static final int adventurer = 20;
    private static final int archmage = 26;
    private static final int soultaker = 25;
    private static final int arcana_lord = 47;
    private static final int cardinal = 20;
    private static final int hierophant = 18;
    private static final int evas_templar = 39;
    private static final int sword_muse = 13;
    private static final int wind_rider = 27;
    private static final int moonlight_sentinel = 22;
    private static final int mystic_muse = 21;
    private static final int elemental_master = 45;
    private static final int evas_saint = 14;
    private static final int shillien_templar = 35;
    private static final int spectral_dancer = 10;
    private static final int ghost_hunter = 33;
    private static final int ghost_sentinel = 20;
    private static final int storm_screamer = 25;
    private static final int spectral_master = 49;
    private static final int shillien_saint = 21;
    private static final int titan = 26;
    private static final int grand_khavatari = 24;
    private static final int dominator = 29;
    private static final int doomcryer = 23;
    private static final int fortune_seeker = 42;
    private static final int maestro = 44;
    private static final int doombringer = 28;
    private static final int m_soul_hound = 36;
    private static final int f_soul_hound = 36;
    private static final int trickster = 30;
    private static final int judicator = 48;

    private static final int threshold = 25;
    private static final int max_threshold = 140;
    private static final int loner = 25;
    private static final int category_weight = 15;
    private static final SkillEntry morale_up_lv1 = SkillTable.getInstance().getSkillEntry(6885, 1);
    private static final SkillEntry morale_up_lv2 = SkillTable.getInstance().getSkillEntry(6885, 2);
    private static final SkillEntry morale_up_lv3 = SkillTable.getInstance().getSkillEntry(6885, 3);
    private int i_ai0;
    private int i_ai1;

    public detect_party_wizard(final NpcInstance actor) {
        super(actor);
        i_ai0 = 0;
        i_ai1 = 0;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (attacker != null) {
            if (i_ai1 == 0) {
                i_ai1 = 1;
                if (attacker.isPlayer() && attacker.getPlayer().getParty() != null) {
                    final Party party = attacker.getPlayer().getParty();
                    for (final Player member : party.getPartyMembers()) {
                        //TODO: Проверить точно. (c)Mangol
                        if (getActor().getDistance(member) > 600)
                            continue;
                        switch (member.getPlayerClassComponent().getClassId()) {
                            case duelist:
                                if (duelist > loner) {
                                    i_ai0 = ((i_ai0 + duelist) - loner);
                                }
                                break;
                            case dreadnought:
                                if (dreadnought > loner) {
                                    i_ai0 = ((i_ai0 + dreadnought) - loner);
                                }
                                break;
                            case phoenix_knight:
                                if (phoenix_knight > loner) {
                                    i_ai0 = ((i_ai0 + phoenix_knight) - loner);
                                }
                                break;
                            case hell_knight:
                                if (hell_knight > loner) {
                                    i_ai0 = ((i_ai0 + hell_knight) - loner);
                                }
                                break;
                            case sagittarius:
                                if (sagittarius > loner) {
                                    i_ai0 = ((i_ai0 + sagittarius) - loner);
                                }
                                break;
                            case adventurer:
                                if (adventurer > loner) {
                                    i_ai0 = ((i_ai0 + adventurer) - loner);
                                }
                                break;
                            case archmage:
                                if (archmage > loner) {
                                    i_ai0 = ((i_ai0 + archmage) - loner);
                                }
                                break;
                            case soultaker:
                                if (soultaker > loner) {
                                    i_ai0 = ((i_ai0 + soultaker) - loner);
                                }
                                break;
                            case arcana_lord:
                                if (arcana_lord > loner) {
                                    i_ai0 = ((i_ai0 + arcana_lord) - loner);
                                }
                                break;
                            case cardinal:
                                if (cardinal > loner) {
                                    i_ai0 = ((i_ai0 + cardinal) - loner);
                                }
                                break;
                            case hierophant:
                                if (hierophant > loner) {
                                    i_ai0 = ((i_ai0 + hierophant) - loner);
                                }
                                break;
                            case eva_templar:
                                if (evas_templar > loner) {
                                    i_ai0 = ((i_ai0 + evas_templar) - loner);
                                }
                                break;
                            case sword_muse:
                                if (sword_muse > loner) {
                                    i_ai0 = ((i_ai0 + sword_muse) - loner);
                                }
                                break;
                            case wind_rider:
                                if (wind_rider > loner) {
                                    i_ai0 = ((i_ai0 + wind_rider) - loner);
                                }
                                break;
                            case moonlight_sentinel:
                                if (moonlight_sentinel > loner) {
                                    i_ai0 = ((i_ai0 + moonlight_sentinel) - loner);
                                }
                                break;
                            case mystic_muse:
                                if (mystic_muse > loner) {
                                    i_ai0 = ((i_ai0 + mystic_muse) - loner);
                                }
                                break;
                            case elemental_master:
                                if (elemental_master > loner) {
                                    i_ai0 = ((i_ai0 + elemental_master) - loner);
                                }
                                break;
                            case eva_saint:
                                if (evas_saint > loner) {
                                    i_ai0 = ((i_ai0 + evas_saint) - loner);
                                }
                                break;
                            case shillien_templar:
                                if (shillien_templar > loner) {
                                    i_ai0 = ((i_ai0 + shillien_templar) - loner);
                                }
                                break;
                            case spectral_dancer:
                                if (spectral_dancer > loner) {
                                    i_ai0 = ((i_ai0 + spectral_dancer) - loner);
                                }
                                break;
                            case ghost_hunter:
                                if (ghost_hunter > loner) {
                                    i_ai0 = ((i_ai0 + ghost_hunter) - loner);
                                }
                                break;
                            case ghost_sentinel:
                                if (ghost_sentinel > loner) {
                                    i_ai0 = ((i_ai0 + ghost_sentinel) - loner);
                                }
                                break;
                            case storm_screamer:
                                if (storm_screamer > loner) {
                                    i_ai0 = ((i_ai0 + storm_screamer) - loner);
                                }
                                break;
                            case spectral_master:
                                if (spectral_master > loner) {
                                    i_ai0 = ((i_ai0 + spectral_master) - loner);
                                }
                                break;
                            case shillien_saint:
                                if (shillien_saint > loner) {
                                    i_ai0 = ((i_ai0 + shillien_saint) - loner);
                                }
                                break;
                            case titan:
                                if (titan > loner) {
                                    i_ai0 = ((i_ai0 + titan) - loner);
                                }
                                break;
                            case grand_khauatari:
                                if (grand_khavatari > loner) {
                                    i_ai0 = ((i_ai0 + grand_khavatari) - loner);
                                }
                                break;
                            case dominator:
                                if (dominator > loner) {
                                    i_ai0 = ((i_ai0 + dominator) - loner);
                                }
                                break;
                            case doomcryer:
                                if (doomcryer > loner) {
                                    i_ai0 = ((i_ai0 + doomcryer) - loner);
                                }
                                break;
                            case fortune_seeker:
                                if (fortune_seeker > loner) {
                                    i_ai0 = ((i_ai0 + fortune_seeker) - loner);
                                }
                                break;
                            case maestro:
                                if (maestro > loner) {
                                    i_ai0 = ((i_ai0 + maestro) - loner);
                                }
                                break;
                            case doombringer:
                                if (doombringer > loner) {
                                    i_ai0 = ((i_ai0 + doombringer) - loner);
                                }
                                break;
                            case m_soul_hound:
                                if (m_soul_hound > loner) {
                                    i_ai0 = ((i_ai0 + m_soul_hound) - loner);
                                }
                                break;
                            case f_soul_hound:
                                if (f_soul_hound > loner) {
                                    i_ai0 = ((i_ai0 + f_soul_hound) - loner);
                                }
                                break;
                            case trickster:
                                if (trickster > loner) {
                                    i_ai0 = ((i_ai0 + trickster) - loner);
                                }
                                break;
                            case judicator:
                                if (judicator > loner) {
                                    i_ai0 = ((i_ai0 + judicator) - loner);
                                }
                                break;
                        }
                        if (member.getPlayerClassComponent().getClassId() == ClassId.hell_knight || member.getPlayerClassComponent().getClassId() == ClassId.eva_templar || member.getPlayerClassComponent().getClassId() == ClassId.shillien_templar) {
                            i_ai0 = (i_ai0 + category_weight);
                        }
                        if (member.getPlayerClassComponent().getClassId() == ClassId.arcana_lord || member.getPlayerClassComponent().getClassId() == ClassId.elemental_master || member.getPlayerClassComponent().getClassId() == ClassId.spectral_master) {
                            i_ai0 = (i_ai0 + category_weight);
                        }
                        if (member.getPlayerClassComponent().getClassId() == ClassId.mystic_muse || member.getPlayerClassComponent().getClassId() == ClassId.storm_screamer) {
                            i_ai0 = (i_ai0 + 3);
                        }
                        if (member.getPlayerClassComponent().getClassId() == ClassId.moonlight_sentinel || member.getPlayerClassComponent().getClassId() == ClassId.ghost_sentinel || member.getPlayerClassComponent().getClassId() == ClassId.trickster) {
                            i_ai0 = (i_ai0 + 3);
                        }
                        if (member.getPlayerClassComponent().getClassId() == ClassId.wind_rider || member.getPlayerClassComponent().getClassId() == ClassId.ghost_hunter) {
                            i_ai0 = (i_ai0 + 3);
                        }
                        if (member.getPlayerClassComponent().getClassId() == ClassId.eva_saint || member.getPlayerClassComponent().getClassId() == ClassId.shillien_saint || member.getPlayerClassComponent().getClassId() == ClassId.dominator) {
                            i_ai0 = (i_ai0 + 1);
                        }
                    }
                    if (i_ai0 > threshold) {
                        for (final Player member : party.getPartyMembers()) {
                            //TODO: Проверить точно. (c)Mangol
                            if (getActor().getDistance(member) > 600)
                                continue;
                            if (i_ai0 > (max_threshold * 0.45)) {
                                member.altOnMagicUseTimer(member, morale_up_lv3);
                            } else if (i_ai0 > (max_threshold * 0.30)) {
                                member.altOnMagicUseTimer(member, morale_up_lv2);
                            } else {
                                member.altOnMagicUseTimer(member, morale_up_lv1);
                            }
                        }
                    }
                    i_ai0 = 0;
                    ThreadPoolManager.getInstance().schedule(() -> {
                        if (getActor() != null && !getActor().isDead()) {
                            i_ai1 = 0;
                        }
                    }, 55000L);
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        if (killer != null && killer.isPlayer()) {
            final MonsterInstance actor = (MonsterInstance) getActor();
            if (actor != null && actor.isSpoiled()) {
                if (Rnd.get(1000) < 5) {
                    int invisible_npc = 18919;
                    NpcUtils.createOnePrivateEx(invisible_npc, actor.getX(), actor.getY(), actor.getZ(), killer, 0, 0);
                }
                if (Rnd.get(2) < 1) {
                    getActor().dropItem(killer.getPlayer(), 8604, 1);
                } else {
                    actor.dropItem(killer.getPlayer(), 8605, 1);
                }
            }
        }
        super.onEvtDead(killer);
    }
}