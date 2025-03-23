package com.questhelper.helpers.mischelpers.farmrun;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import com.questhelper.QuestHelperConfig;
import com.questhelper.helpers.mischelpers.farmrun.utils.PatchImplementation;
import com.questhelper.panel.DropdownRenderer;
import com.questhelper.questinfo.HelperConfig;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.Text;

public class SeedsHelperConfig extends HelperConfig {

    private Collection<PatchImplementation> selectedPatches;
    private final JPanel filtersPanel = new JPanel(new SpringLayout());
    private FarmingSeedFactory farmingSeedFactory;

    public SeedsHelperConfig(FarmingSeedFactory seedFactory, String name, Collection<PatchImplementation> selectedPatches) {
        // empty key and null enum as customRender implements using PatchImplementation
        super(name, "", null);
        this.farmingSeedFactory = seedFactory;
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

    private JComboBox<FarmingSeed> makeNewDropdown(ConfigManager configManager, FarmingSeed[] values, String key) {
        JComboBox<FarmingSeed> dropdown = new JComboBox<>(values);
        dropdown.setFocusable(false);
        dropdown.setForeground(Color.WHITE);
        dropdown.setRenderer(new DropdownRenderer());
        dropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                FarmingSeed source = (FarmingSeed) e.getItem();
                configManager.setRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP,
                        key,
                        source.getSeedName());
            }
        });
        String currentVal = configManager.getRSProfileConfiguration(QuestHelperConfig.QUEST_BACKGROUND_GROUP, key);
        for (FarmingSeed value : values) {
            if (value.getSeedName().equals(currentVal)) {
                dropdown.setSelectedItem(value);
            }
        }

        return dropdown;
    }

    private void renderInternal(ConfigManager configManager) {
        filtersPanel.removeAll();
        for (PatchImplementation patch : selectedPatches) {

            JComboBox<FarmingSeed> dropdown = makeNewDropdown(configManager, farmingSeedFactory.getValidSeeds(patch),
                    makeConfigKey(patch));
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

        filtersPanel.setVisible(selectedPatches.size() > 0);
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

    private static String makeConfigKey(PatchImplementation patch) {
        return "farmrun_seed_" + patch.name().toLowerCase();
    }
}