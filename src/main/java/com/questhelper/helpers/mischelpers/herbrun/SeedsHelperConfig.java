package com.questhelper.helpers.mischelpers.herbrun;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import com.questhelper.QuestHelperConfig;
import com.questhelper.panel.DropdownRenderer;
import com.questhelper.questinfo.HelperConfig;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.Text;

public class SeedsHelperConfig extends HelperConfig {

    private List<PatchImplementation> selectedPatches;
    private final JPanel filtersPanel = new JPanel(new SpringLayout());

    public SeedsHelperConfig(String name, String key, Enum[] enums, List<PatchImplementation> selectedPatches) {
        super(name, key, enums);
        this.setCustomRender(true);
        this.selectedPatches = selectedPatches;
        this.filtersPanel.setMinimumSize(new Dimension(PluginPanel.PANEL_WIDTH, 0));
    }

    @Override
    public Component render(ConfigManager configManager) {
        renderInternal(configManager);
        return filtersPanel;
    }

    public void refresh(ConfigManager configManager) {        
        renderInternal(configManager);
    }

    private JComboBox<Enum> makeNewDropdown(ConfigManager configManager, Enum[] values, String key) {
        JComboBox<Enum> dropdown = new JComboBox<>(values);
        dropdown.setFocusable(false);
        dropdown.setForeground(Color.WHITE);
        dropdown.setRenderer(new DropdownRenderer());
        dropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Enum source = (Enum) e.getItem();
                configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                        key,
                        source);
            }
        });
        String currentVal = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key);
        for (Enum value : values) {
            if (value.name().equals(currentVal)) {
                dropdown.setSelectedItem(value);
            }
        }

        return dropdown;
    }

    private void renderInternal(ConfigManager configManager) {
        filtersPanel.removeAll();
        for (PatchImplementation patch : selectedPatches) {
            JComboBox<Enum> dropdown = makeNewDropdown(configManager, this.getEnums(), patch.name());
            JLabel p = new JLabel(Text.titleCase(patch), JLabel.TRAILING);
            p.setForeground(Color.WHITE);
            p.setLabelFor(dropdown);
            filtersPanel.add(p);
            filtersPanel.add(dropdown);
        }
        makeCompactGrid(filtersPanel,
                selectedPatches.size(), 2,
                6, 6,
                6, 6);
    }

    public void setSelectedPatches(List<PatchImplementation> selectedPatches) {
        this.selectedPatches = selectedPatches;
        this.filtersPanel.repaint();
    }

    /* Used by makeCompactGrid. */
    private static SpringLayout.Constraints getConstraintsForCell(
            int row, int col,
            Container parent,
            int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    /**
     * Aligns the first <code>rows</code> * <code>cols</code>
     * components of <code>parent</code> in
     * a grid. Each component in a column is as wide as the maximum
     * preferred width of the components in that column;
     * height is similarly determined for each row.
     * The parent is made just big enough to fit them all.
     *
     * @param rows     number of rows
     * @param cols     number of columns
     * @param initialX x location to start the grid at
     * @param initialY y location to start the grid at
     * @param xPad     x padding between cells
     * @param yPad     y padding between cells
     */
    private static void makeCompactGrid(Container parent,
            int rows, int cols,
            int initialX, int initialY,
            int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout) parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        // Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                        getConstraintsForCell(r, c, parent, cols).getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        // Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                        getConstraintsForCell(r, c, parent, cols).getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        // Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }
}