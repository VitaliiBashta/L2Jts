package org.mmocore.commons.configuration.setup;

@FunctionalInterface
public interface ISetup {
    /**
     * Используется непосредственно после заполнения основных переменных, делая конфиги более гибкими. Делая финт ушами.
     */
    void setup();
}
