/*
 *     TSingleton<L2UserHardWareInformation>::GetObj(v11);
    v2 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 212) > 1;
    v3 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 100);
    v25 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 88);
    v24 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 84);
    v23 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 80);
    v22 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 76);
    v21 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 72);
    v20 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 68);
    v19 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 64);
    v18 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 96);
    v17 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 92);
    v16 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 28);
    v15 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 24);
    v14 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 20);
    v13 = *(_DWORD *)(TSingleton<L2UserHardWareInformation>::GetObj(v11) + 16);
    v12 = **((_DWORD **)v26 + 18);
    v4 = TSingleton<L2UserHardWareInformation>::GetObj(v11);
    v5 = L2UserHardWareInformation::GetVgaDriverVersion(v4);
    v6 = TSingleton<L2UserHardWareInformation>::GetObj(v5);
    L2UserHardWareInformation::GetVgaName(v6);
    v7 = TSingleton<L2UserHardWareInformation>::GetObj(v19);
    L2UserHardWareInformation::GetCPUBrandString(v7);
    v8 = TSingleton<L2UserHardWareInformation>::GetObj(v13);
    L2UserHardWareInformation::GetMacAddress(v8);
    v9 = *((_DWORD *)v26 + 18);
    (*(void (**)(void))(v12 + 108))();
    FOutputDevice::Logf(*(_DWORD *)GNetworkLog, L"(Send)HardWareInfo", v9);
    v10 = TSingleton<L2UserHardWareInformation>::GetObj(v11);
    result = L2UserHardWareInformation::SendHardWareOK(v10);
 */
package org.mmocore.gameserver.network.lineage.clientpackets;

import org.jts.protection.Protection;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 * chSddddddSddddddddddSS
 */
public class RequestHardWareInfo extends L2GameClientPacket {
    private String mac;
    private String cpu;
    private String vgaName;
    private String driverVersion;
    private int windowsPlatformId;
    private int windowsMajorVersion;
    private int windowsMinorVersion;
    private int windowsBuildNumber;
    private int DXVersion;
    private int DXRevision;
    private int cpuSpeed;
    private int cpuCoreCount;
    private int unk8;
    private int unk9;
    private int PhysMemory1;
    private int PhysMemory2;
    private int unk12;
    private int videoMemory;
    private int unk14;
    private int vgaVersion;

    @Override
    protected void readImpl() throws Exception {
        mac = readS();
        windowsPlatformId = readD();
        windowsMajorVersion = readD();
        windowsMinorVersion = readD();
        windowsBuildNumber = readD();
        DXVersion = readD();
        DXRevision = readD();
        cpu = readS();
        cpuSpeed = readD();
        cpuCoreCount = readD();
        unk8 = readD();
        unk9 = readD();
        PhysMemory1 = readD();
        PhysMemory2 = readD();
        unk12 = readD();
        videoMemory = readD();
        unk14 = readD();
        vgaVersion = readD();
        vgaName = readS();
        driverVersion = readS();
    }

    @Override
    protected void runImpl() throws Exception {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isGM() && Protection.isProtectEnabled()) {
            _log.info("Mac: {} WPI: {} WMV1: {} WMV2: {} WBN: {} DXV: {} DXR: {} CPU: {} CPUS: {} CPUCC: {} PM1: {} PM2: {} VM: {} VGAV: {} VGAN: {} DV: {}",
                    mac, windowsPlatformId, windowsMajorVersion, windowsMinorVersion, windowsBuildNumber, DXVersion, DXRevision, cpu, cpuSpeed,
                    cpuCoreCount, PhysMemory1, PhysMemory2, videoMemory, vgaVersion, vgaName, driverVersion);
            _log.info("UNK8: {} UNK9: {} UNK12: {} UNK14: {}", unk8, unk9, unk12, unk14);
        }
    }
}