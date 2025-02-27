package net.satisfy.smoker.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.satisfy.smoker.Smoker;
import net.satisfy.smoker.client.menu.SmokingFoodMenu;

import java.util.function.Supplier;

public class ScreenHandlerTypeRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Smoker.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<SmokingFoodMenu>> SMOKING_FOOD_MENU = create("smoking_food_menu",
            () -> new MenuType<>(SmokingFoodMenu::new, FeatureFlags.VANILLA_SET));

    private static <T extends MenuType<?>> RegistrySupplier<T> create(String name, Supplier<T> type) {
        return MENU_TYPES.register(name, type);
    }

    public static void init() {
        MENU_TYPES.register();
    }
}
