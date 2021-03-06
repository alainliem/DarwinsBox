/*
 * Copyright (C) 2013 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package darwin.resourcehandling.shader.brdf;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class BoolOption implements Option {

    private final String name;
    private final boolean defaultt;
    private boolean value;

    public BoolOption(String name, boolean defaultt) {
        this.name = name;
        this.defaultt = value = defaultt;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return Boolean.toString(value);
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getDefaultValue() {
        return defaultt;
    }

    @Override
    public String getType() {
        return "bool";
    }
}
