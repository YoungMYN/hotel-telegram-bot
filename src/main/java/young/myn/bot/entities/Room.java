package young.myn.bot.entities;

import jakarta.persistence.*;
import young.myn.bot.enums.RoomType;

@Entity
@Table(name = "rooms")
public class Room {
    private Integer id;
    private RoomType type;
    private String description;
    public Room(){}

    public Room(RoomType type, String description) {
        this.type = type;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
