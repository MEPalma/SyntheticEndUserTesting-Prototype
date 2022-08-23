package naming;

import java.awt.*;
import java.lang.ref.WeakReference;

public class NamedComponent<T extends Component> {

    private String logicalName;
    private final WeakReference<T> component;

    public NamedComponent(String logicalName, T component) {
        this.logicalName = logicalName;
        this.component = new WeakReference<>(component);
    }

    public T getComponent() {
        return component.get();
    }

    public String getLogicalName() {
        return logicalName;
    }

    public boolean wasLastActive() {
        return logicalName.startsWith(Naming.ACTIVE_PREFIX);
    }

    public boolean wasCleared() {
        return component.get() == null;
    }

    public void refreshLogicalName() {
        this.logicalName = NamedComponent.compileLogicalName(component.get());
    }

    @Override
    public String toString() {
        String type = component.getClass().getTypeName();
        T obj = component.get();
        String objStr = (obj != null) ? obj.getClass().getSimpleName() : "WeakReference to dispose";
        return "{NamedComponent<" + type + ">, logicalName='" + logicalName + "', component='" + objStr + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NamedComponent<?> namedComponent)
            return hashCode() == namedComponent.hashCode();
        return false;
    }

    @Override
    public int hashCode() {
        return logicalName.hashCode();
    }

    public static String compileLogicalName(Component component) {
        return Naming.getLogicalName(component);
    }

    public static <T extends Component> NamedComponent<T> namedComponentOf(T component) {
        String logicalName = compileLogicalName(component);
        return new NamedComponent<>(logicalName, component);
    }

}
