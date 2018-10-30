package org.mmocore.gameserver.phantoms.template;

import org.mmocore.gameserver.phantoms.ai.PhantomAiType;
import org.mmocore.gameserver.templates.item.ItemTemplate;

/**
 * Created by Hack
 * Date: 21.08.2016 6:04
 */
public class PhantomTemplate {
    private int classId, race, sex, face, hair, nameColor, titleColor;
    private String name, title;
    private PhantomAiType type;
    private ItemTemplate.Grade grade;

    public PhantomTemplate() {

    }

    public PhantomTemplate(int classId, int race, int sex, int face, int hair, int nameColor, int titleColor,
                           String name, String title, PhantomAiType type, ItemTemplate.Grade grade) {
        this.classId = classId;
        this.race = race;
        this.sex = sex;
        this.face = face;
        this.hair = hair;
        this.nameColor = nameColor;
        this.titleColor = titleColor;
        this.name = name;
        this.title = title;
        this.type = type;
        this.grade = grade;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getHair() {
        return hair;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public int getNameColor() {
        return nameColor;
    }

    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PhantomAiType getType() {
        return type;
    }

    public void setType(PhantomAiType type) {
        this.type = type;
    }

    public ItemTemplate.Grade getGrade() {
        return grade;
    }

    public void setGrade(ItemTemplate.Grade grade) {
        this.grade = grade;
    }
}
