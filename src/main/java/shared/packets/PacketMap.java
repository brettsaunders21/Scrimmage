package shared.packets;

import java.util.UUID;

public class PacketMap extends Packet {

  private UUID uuid;
  private String name;

  public PacketMap(String name, UUID uuid) {
    packetID = PacketID.MAP.getID();
    this.name = name;
    this.uuid = uuid;
    data = packetID + "," + name;
  }

  public PacketMap(String data) {
    String[] unpackedData = data.split(",");
    this.packetID = Integer.parseInt(unpackedData[0]);
    this.name = unpackedData[1];
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }
}
