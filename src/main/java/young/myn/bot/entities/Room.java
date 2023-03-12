package young.myn.bot.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    private Integer id;
    private String description;
    public Room(){}
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

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
