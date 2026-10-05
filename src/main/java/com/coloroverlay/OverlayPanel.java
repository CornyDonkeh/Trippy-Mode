package com.coloroverlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import net.runelite.api.Client;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;

final class OverlayPanel extends PluginPanel
{
    private final Client client;
    private final ColorPickerManager picker;
    private final ColorOverlayConfig config;
    private final OverlayControls controls;
    private boolean refreshing;
    private boolean active = true;
    private final JCheckBox enabled = new JCheckBox("Enable overlay");
    private final JCheckBox trippy = new JCheckBox("Trippy Mode");
    private final JButton tint = new JButton();
    private final JSpinner strength = spinner(25, 0, 100);
    private final JComboBox<TrippyStyle> styles = new JComboBox<>(TrippyStyle.values());
    private final JSpinner cycle = spinner(15, 2, 120);
    private final JComboBox<Object> palettes = new JComboBox<>();
    private final JSpinner count = spinner(6, 1, 16);
    private final JPanel swatches = new JPanel(new GridLayout(0, 2, 6, 6));
    private final JTextField name = new JTextField();
    private final JButton save = new JButton("Save / update palette");
    private final JButton delete = new JButton("Delete saved palette");
    private final JLabel status = new JLabel(" ");
    private final JSpinner points = spinner(6, 3, 24);
    private final JSpinner layers = spinner(3, 1, 12);

    OverlayPanel(Client client, ColorPickerManager picker, ColorOverlayConfig config, OverlayControls controls)
    {
        this.client = client; this.picker = picker; this.config = config; this.controls = controls;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JLabel("Trippy Mode")); add(Box.createVerticalStrut(10));
        add(enabled); row("Tint color", tint); row("Tint strength (%)", strength);
        add(trippy); row("Trippy style", styles); row("Effect cycle (seconds)", cycle);
        row("Color palette", palettes); row("Number of colors", count);
        add(Box.createVerticalStrut(8)); add(swatches);
        row("Palette name", name); add(save); add(delete); add(status);
        row("Kaleidoscope points", points); row("Kaleidoscope layers", layers);
        add(new JLabel("<html>Click a color to edit it.<br>Editing a preset creates a custom copy.<br>Save uses the currently visible colors.<br>Same name updates an existing palette.</html>"));
        palettes.setRenderer(new DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
                Object label = value instanceof PaletteLibrary.Entry ? "Saved: " + ((PaletteLibrary.Entry) value).name : value;
                return super.getListCellRendererComponent(list, label, index, selected, focus);
            }
        });
        enabled.addActionListener(e -> { if (!refreshing) { controls.set("enabled", enabled.isSelected()); } });
        trippy.addActionListener(e -> { if (!refreshing) { controls.set("trippyMode", trippy.isSelected()); } });
        strength.addChangeListener(e -> change("strength", strength));
        cycle.addChangeListener(e -> change("cycleSeconds", cycle));
        points.addChangeListener(e -> change("points", points));
        layers.addChangeListener(e -> change("layers", layers));
        styles.addActionListener(e -> { if (!refreshing) { controls.set("trippyStyle", styles.getSelectedItem()); refresh(); } });
        palettes.addActionListener(e -> {
            if (refreshing) { return; }
            Object selected = palettes.getSelectedItem();
            if (selected instanceof PaletteLibrary.Entry) { controls.selectSaved((PaletteLibrary.Entry) selected); }
            else if (selected instanceof PalettePreset) { controls.selectPreset((PalettePreset) selected); }
            refresh();
        });
        count.addChangeListener(e -> { if (!refreshing) { controls.count((int) count.getValue()); refresh(); } });
        tint.addActionListener(e -> {
            RuneliteColorPicker dialog = picker.create(client, config.color(), "Tint color", false);
            dialog.setOnClose(color -> { if (active) { controls.set("color", color); refresh(); } });
            dialog.setVisible(true);
        });
        save.addActionListener(e -> {
            try { controls.save(name.getText()); refresh(); status.setText("Palette saved."); }
            catch (IllegalArgumentException exception) { status.setText("Use a name with 1-40 characters."); }
        });
        delete.addActionListener(e -> {
            if (!config.savedPaletteName().isEmpty()) {
                controls.delete(config.savedPaletteName()); name.setText(""); refresh(); status.setText("Saved palette deleted.");
            }
        });
        refresh();
    }

    private static JSpinner spinner(int value, int min, int max) { return new JSpinner(new SpinnerNumberModel(value, min, max, 1)); }
    private void change(String key, JSpinner spinner) { if (!refreshing) { controls.set(key, spinner.getValue()); } }
    private void row(String label, JComponent component)
    {
        add(Box.createVerticalStrut(8)); add(new JLabel(label));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); add(component);
    }
    void disposePanel() { active = false; }

    void refresh()
    {
        if (!active) { return; }
        refreshing = true;
        try
        {
            enabled.setSelected(config.enabled()); trippy.setSelected(config.trippyMode());
            strength.setValue(Math.max(0, Math.min(100, config.strength())));
            cycle.setValue(Math.max(2, Math.min(120, config.cycleSeconds())));
            points.setValue(Math.max(3, Math.min(24, config.points())));
            layers.setValue(Math.max(1, Math.min(12, config.layers())));
            count.setValue(Math.max(1, Math.min(16, config.colorCount())));
            styles.setSelectedItem(config.trippyStyle());
            boolean kaleido = config.trippyStyle().name().startsWith("KALEIDOSCOPE");
            points.setEnabled(kaleido); layers.setEnabled(kaleido);
            updateColor(tint, config.color());
            palettes.removeAllItems();
            for (PalettePreset preset : PalettePreset.values()) { palettes.addItem(preset); }
            List<PaletteLibrary.Entry> saved = PaletteLibrary.read(config.savedPalettes());
            Object selected = config.palette();
            for (PaletteLibrary.Entry entry : saved) {
                palettes.addItem(entry);
                if (entry.name.equals(config.savedPaletteName())) { selected = entry; }
            }
            palettes.setSelectedItem(selected);
            delete.setEnabled(selected instanceof PaletteLibrary.Entry);
            if (selected instanceof PaletteLibrary.Entry) { name.setText(((PaletteLibrary.Entry) selected).name); }
            swatches.removeAll();
            Color[] colors = controls.colors();
            for (int i = 0; i < colors.length; i++)
            {
                final int index = i;
                JButton button = new JButton("Color " + (i + 1));
                updateColor(button, colors[i]);
                button.addActionListener(e -> {
                    String original = config.customColors();
                    PalettePreset preset = config.palette();
                    int size = config.colorCount();
                    RuneliteColorPicker dialog = picker.create(client, colors[index], "Palette color " + (index + 1), false);
                    dialog.setOnClose(color -> {
                        // A picker opened for an old palette should not modify a newly selected palette.
                        if (active && preset == config.palette() && size == config.colorCount()
                            && Objects.equals(original, config.customColors())) {
                            controls.editColor(index, color); refresh();
                        }
                    });
                    dialog.setVisible(true);
                });
                swatches.add(button);
            }
            revalidate(); repaint();
        }
        finally { refreshing = false; }
    }

    private static void updateColor(JButton button, Color color)
    {
        button.setBackground(color); button.setOpaque(true); button.setBorderPainted(false);
        button.setForeground(color.getRed() * .299 + color.getGreen() * .587 + color.getBlue() * .114 > 140 ? Color.BLACK : Color.WHITE);
        button.setToolTipText(String.format("#%06X - click to choose", color.getRGB() & 0xffffff));
        if (!button.getText().startsWith("Color ")) { button.setText(String.format("#%06X", color.getRGB() & 0xffffff)); }
    }
}
