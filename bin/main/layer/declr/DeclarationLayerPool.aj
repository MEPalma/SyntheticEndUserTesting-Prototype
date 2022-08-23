package layer.declr;

import layer.state.StateLayer;
import lifecycle.lci.lcistate.LCIState;
import lifecycle.lci.lcistate.LCIStateActiveActions;
import naming.NamedComponent;

import java.awt.*;
import java.util.*;
import java.util.List;


public aspect DeclarationLayerPool implements StateLayer {
    private final List<DeclarationLayer<Component>> declarationLayers;
    private final Map<Component, NamedComponent<Component>> activeComponents;

    public DeclarationLayerPool() {
        this.declarationLayers = new ArrayList<>(3);
        this.declarationLayers.add(MouseDeclrLayer.aspectOf());
        this.declarationLayers.add(KeyboardDeclrLayer.aspectOf());
        this.declarationLayers.add(ToggleDeclrLayer.aspectOf());
        //
        this.activeComponents = new HashMap<>();
    }

    pointcut isAddComponent(Component parent, Component child):
            call(* Component+.add*(Component, ..)) && args(child, ..) && target(parent);

    after (Component parent, Component child): isAddComponent(parent, child) {
        synchronized (this.activeComponents) {
            for (DeclarationLayer<Component> declarationLayer : this.declarationLayers)
                declarationLayer.refreshLogicalNames();
            updateActiveComponents();
        }
    }

    private void updateActiveComponents() {
        synchronized (this.activeComponents) {
            this.activeComponents.clear();
            for (DeclarationLayer<Component> declarationLayer : this.declarationLayers) {
                for (var activeCmp : declarationLayer.getActiveComponents()) {
                    var component = activeCmp.getComponent();
                    if (component != null)
                        this.activeComponents.put(component, activeCmp);
                }
            }
        }
    }

    public NamedComponent<Component> getActiveNamedComponent(Component component) {
        synchronized (this.activeComponents) {
            return this.activeComponents.getOrDefault(component, null);
        }
    }

    public Component getActiveComponent(String logicalName) {
        Component component = null;
        if (logicalName != null)
            synchronized (this.activeComponents) {
                for (var namedComponent : this.activeComponents.values()) {
                    component = namedComponent.getComponent();
                    if (component != null && logicalName.equals(namedComponent.getLogicalName()))
                        break;
                }
            }
        return component;
    }

    public Set<String> getActiveActions() {
        synchronized (this.activeComponents) {
            Set<String> actives = new HashSet<>(activeComponents.size());
            for (var active : activeComponents.values()) {
                String actionName = active.getLogicalName();
                if (!active.wasCleared())
                    actives.add(actionName);
            }
            return actives;
        }
    }

    @Override
    public LCIState getState() {
        Set<String> lastActive = getActiveActions();
        return new LCIStateActiveActions(lastActive);
    }
}
