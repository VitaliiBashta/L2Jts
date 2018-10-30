package org.mmocore.gameserver.ai.maker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KilRoy
 */
public class default_maker {
    private final String maker_name;
    private final int maximum_npc;
    private final myself myself = new myself();
    /*ai_parameters*/
    public final int on_start_spawn = 1;
    private final AtomicInteger npc_count = new AtomicInteger();
    private final List<spawn_define> spawn_define = new ArrayList<>();

    public default_maker(final String maker_name, final int maximum_npc_count) {
        this.maker_name = maker_name;
        this.maximum_npc = maximum_npc_count;
    }

    public String getMakerName() {
        return maker_name;
    }

    public myself getMySelf() {
        return myself;
    }

    public void addSpawnDefine(final spawn_define def) {
        spawn_define.add(def);
    }

    /**
     * Выполняется на старте сервера
     * В зависимости от AI параметра on_start_spawn, может не спавнить объект на старте(отложенный спавн)
     */
    public void onStart() {
        if (on_start_spawn == 0) {
            return;
        }
        for (final spawn_define def : spawn_define) {
            if (maximum_npc >= npc_count.get() + def.getTotal()) {
                if (myself.AtomicIncreaseTotal(def, def.getTotal())) {
                    def.spawn(def.getTotal(), 0, 0);
                }
            }
        }
    }

    /**
     * Выполняется при удалении\деспавне объекта.
     *
     * @param spawn_define - лист спавна удаляемого объекта
     * @param param        - дополнительные параметры
     */
    public void onNpcDeleted(final spawn_define deleted_def, final Object... param) {
        if (deleted_def.getRespawnTime() != 0) {
            if ((maximum_npc >= npc_count.get() + 1) && myself.AtomicIncreaseTotal(deleted_def, 1)) {
                //deleted_def.respawn(1, resp, respRnd);
            } else {
                //deleted_def.despawn();
            }
        }
    }

    /**
     * Выполняется при полном удалении объектов из мейкера
     */
    public void onAllNpcDelete() {

    }

    /**
     * Выполняется в случае передачи события в мейкер(спавн\деспавн и все что обрабатывается в мейкере)
     *
     * @param script_event_id - ID события
     * @param arg             - дополнительные аргументы
     */
    public void onScriptEvent(final int script_event_id, final Object... arg) {
        switch (script_event_id) {
            case 1000: // деспавн объекта
                for (final spawn_define deleted_def : spawn_define) {
                    //deleted_def.despawn();
                }
                break;
            case 1001: // спавн объекта
                for (final spawn_define def : spawn_define) {
                    final int i1 = def.getTotal() - def.getNpcCount().get();
                    {
                        if (i1 > 0) {
                            if ((maximum_npc >= npc_count.get() + i1) && myself.AtomicIncreaseTotal(def, i1)) {
                                //def.spawn(i1, arg[0], 0);
                            }
                        }
                    }
                }
                break;
        }
    }

    final class myself {
        public final boolean AtomicIncreaseTotal(final spawn_define def, final int def_total) {
            if (maximum_npc >= npc_count.get() + def_total && def.getTotal() >= def.getNpcCount().get() + def_total) {
                npc_count.set(npc_count.get() + def_total);
                def.getNpcCount().set(npc_count.get() + def_total);
                return true;
            }
            return false;
        }

        public final boolean AtomicIncreaseTotal(final int def_total) {
            if (maximum_npc >= npc_count.get() + def_total) {
                npc_count.set(npc_count.get() + def_total);
                return true;
            }
            return false;
        }

        public final boolean AtomicDecreaseTotal(final spawn_define def, final int def_total) {
            if (npc_count.get() >= def_total && def.getNpcCount().get() >= def_total) {
                npc_count.set(npc_count.get() - def_total);
                def.getNpcCount().set(npc_count.get() - def_total);
                return true;
            }
            return false;
        }

        public final boolean AtomicDecreaseTotal(final int def_total) {
            if (npc_count.get() >= def_total) {
                npc_count.set(npc_count.get() - def_total);
                return true;
            }
            return false;
        }
    }
}