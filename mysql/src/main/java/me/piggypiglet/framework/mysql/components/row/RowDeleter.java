package me.piggypiglet.framework.mysql.components.row;

import me.piggypiglet.framework.mysql.components.MySQLComponent;
import me.piggypiglet.framework.mysql.components.row.objects.KeyValueSet;

// ------------------------------
// Copyright (c) PiggyPiglet 2019
// https://www.piggypiglet.me
// ------------------------------
public final class RowDeleter extends MySQLComponent {
    private final String table;
    private final KeyValueSet location;

    private RowDeleter(String table, KeyValueSet location) {
        this.table = table;
        this.location = location;
    }

    public static Builder builder(String table) {
        return new Builder(table);
    }

    public static class Builder {
        private final String table;
        private KeyValueSet location;

        private Builder(String table) {
            this.table = table;
        }

        public Builder location(KeyValueSet location) {
            this.location = location;
            return this;
        }

        public RowDeleter build() {
            return new RowDeleter(table, location);
        }
    }

    public boolean execute() {
        return delete(table, location);
    }
}
