package layer.declr;

import naming.NamedComponent;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.*;

public abstract class DeclarationLayer<T extends Component> {
    private final Map<WeakReference<Component>, NamedComponent<T>> namedComponentByComponent;

    public DeclarationLayer() {
        this.namedComponentByComponent = new HashMap<>();
    }

    public synchronized NamedComponent<T> getNamedComponent(T component) {
        var tmpWeakRef = new WeakReference<>(component);
        return this.namedComponentByComponent.getOrDefault(tmpWeakRef, null);
    }

    public synchronized NamedComponent<T> putNamedComponent(T component) {
        var namedComponent = getNamedComponent(component);
        if (namedComponent == null) {
            namedComponent = NamedComponent.namedComponentOf(component);
            putNamedComponent(namedComponent);
        }
        return namedComponent;
    }

    private synchronized void putNamedComponent(NamedComponent<T> namedComponent) {
        WeakReference<Component> componentWeakReference = new WeakReference<>(namedComponent.getComponent());
        this.namedComponentByComponent.put(componentWeakReference, namedComponent);
        removeCleared();
    }

    private synchronized void removeCleared() {
        List<WeakReference<Component>> clearedReferences = new LinkedList<>();
        for (var ref : this.namedComponentByComponent.keySet())
            if (ref.get() == null)
                clearedReferences.add(ref);
        for (var clearedRef : clearedReferences)
            this.namedComponentByComponent.remove(clearedRef);
    }

    public synchronized void refreshLogicalNames() {
        removeCleared();
        for (var namedComponent : this.namedComponentByComponent.values())
            namedComponent.refreshLogicalName();
    }

    public synchronized List<NamedComponent<T>> getActiveComponents() {
        removeCleared();
        List<NamedComponent<T>> active = new LinkedList<>();
        for (var namedComponent : this.namedComponentByComponent.values())
            if (namedComponent.wasLastActive())
                active.add(namedComponent);
        return active;
    }

}
