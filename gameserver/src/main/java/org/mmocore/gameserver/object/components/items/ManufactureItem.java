package org.mmocore.gameserver.object.components.items;

public class ManufactureItem {
    private final int recipeId;
    private final long cost;

    public ManufactureItem(int recipeId, long cost) {
        this.recipeId = recipeId;
        this.cost = cost;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public long getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return ((ManufactureItem) o).getRecipeId() == this.getRecipeId();
    }

    @Override
    public int hashCode() {
        return recipeId;
    }
}
