package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import cc.brainbook.android.colorpicker.ColorPickerView;
import cc.brainbook.android.colorpicker.OnColorChangedListener;
import cc.brainbook.android.colorpicker.OnColorSelectedListener;
import cc.brainbook.android.colorpicker.Utils;
import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorWheelRendererBuilder;
import cc.brainbook.android.colorpicker.renderer.ColorWheelRenderer;
import cc.brainbook.android.colorpicker.slider.AlphaSlider;
import cc.brainbook.android.colorpicker.slider.LightnessSlider;
import cc.brainbook.android.richeditortoolbar.R;

public class BaseColorPickerDialogBuilder extends BaseDialogBuilder {
    protected LinearLayout pickerContainer;
    protected ColorPickerView colorPickerView;
    protected Integer[] initialColor = new Integer[]{null, null, null, null, null};

    private LightnessSlider lightnessSlider;
    private AlphaSlider alphaSlider;
    private EditText colorEdit;
    private LinearLayout colorPreview;
    private boolean isLightnessSliderEnabled = true;
    private boolean isAlphaSliderEnabled = true;
    private boolean isBorderEnabled = true;
    private boolean isColorEditEnabled = false;
    private boolean isPreviewEnabled = false;
    private int pickerCount = 1;


    public BaseColorPickerDialogBuilder initialColor(int initialColor) {
        this.initialColor[0] = initialColor;
        return this;
    }

    public BaseColorPickerDialogBuilder initialColors(@NonNull int[] initialColor) {
        for (int i = 0; i < initialColor.length && i < this.initialColor.length; i++) {
            this.initialColor[i] = initialColor[i];
        }

        return this;
    }

    public BaseColorPickerDialogBuilder wheelType(ColorPickerView.WHEEL_TYPE wheelType) {
        ColorWheelRenderer renderer = ColorWheelRendererBuilder.getRenderer(wheelType);
        colorPickerView.setRenderer(renderer);
        return this;
    }

    public BaseColorPickerDialogBuilder density(int density) {
        colorPickerView.setDensity(density);
        return this;
    }

    public BaseColorPickerDialogBuilder setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        colorPickerView.addOnColorChangedListener(onColorChangedListener);
        return this;
    }

    public BaseColorPickerDialogBuilder setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        colorPickerView.addOnColorSelectedListener(onColorSelectedListener);
        return this;
    }

    public BaseColorPickerDialogBuilder noSliders() {
        isLightnessSliderEnabled = false;
        isAlphaSliderEnabled = false;
        return this;
    }

    public BaseColorPickerDialogBuilder alphaSliderOnly() {
        isLightnessSliderEnabled = false;
        isAlphaSliderEnabled = true;
        return this;
    }

    public BaseColorPickerDialogBuilder lightnessSliderOnly() {
        isLightnessSliderEnabled = true;
        isAlphaSliderEnabled = false;
        return this;
    }

    public BaseColorPickerDialogBuilder showAlphaSlider(boolean showAlpha) {
        isAlphaSliderEnabled = showAlpha;
        return this;
    }

    public BaseColorPickerDialogBuilder showLightnessSlider(boolean showLightness) {
        isLightnessSliderEnabled = showLightness;
        return this;
    }

    public BaseColorPickerDialogBuilder showBorder(boolean showBorder) {
        isBorderEnabled = showBorder;
        return this;
    }

    public BaseColorPickerDialogBuilder showColorEdit(boolean showEdit) {
        isColorEditEnabled = showEdit;
        return this;
    }

    public BaseColorPickerDialogBuilder setColorEditTextColor(int argb) {
        colorPickerView.setColorEditTextColor(argb);
        return this;
    }

    public BaseColorPickerDialogBuilder showColorPreview(boolean showPreview) {
        isPreviewEnabled = showPreview;
        if (!showPreview)
            pickerCount = 1;
        return this;
    }

    public BaseColorPickerDialogBuilder setPickerCount(int pickerCount) throws IndexOutOfBoundsException {
        if (pickerCount < 1 || pickerCount > 5)
            throw new IndexOutOfBoundsException("Picker Can Only Support 1-5 Colors");
        this.pickerCount = pickerCount;
        if (this.pickerCount > 1)
            this.isPreviewEnabled = true;
        return this;
    }

    public BaseColorPickerDialogBuilder setPositiveButton(CharSequence text, final ColorPickerClickListener onClickListener) {
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positiveButtonOnClick(dialog, onClickListener);
            }
        });
        return this;
    }

    public BaseColorPickerDialogBuilder setPositiveButton(int textId, final ColorPickerClickListener onClickListener) {
        builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                positiveButtonOnClick(dialog, onClickListener);
            }
        });
        return this;
    }

    protected void setupColorPicker(@NonNull Context context) {
        colorPickerView.setInitialColors(initialColor, getStartOffset(initialColor));
        colorPickerView.setShowBorder(isBorderEnabled);

        if (isLightnessSliderEnabled) {
            LinearLayout.LayoutParams layoutParamsForLightnessBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, cc.brainbook.android.colorpicker.R.dimen.default_slider_height));
            lightnessSlider = new LightnessSlider(context);
            lightnessSlider.setLayoutParams(layoutParamsForLightnessBar);
            pickerContainer.addView(lightnessSlider);
            colorPickerView.setLightnessSlider(lightnessSlider);
            lightnessSlider.setColor(getStartColor(initialColor));
            lightnessSlider.setShowBorder(isBorderEnabled);
        }
        if (isAlphaSliderEnabled) {
            LinearLayout.LayoutParams layoutParamsForAlphaBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, cc.brainbook.android.colorpicker.R.dimen.default_slider_height));
            alphaSlider = new AlphaSlider(context);
            alphaSlider.setLayoutParams(layoutParamsForAlphaBar);
            pickerContainer.addView(alphaSlider);
            colorPickerView.setAlphaSlider(alphaSlider);
            alphaSlider.setColor(getStartColor(initialColor));
            alphaSlider.setShowBorder(isBorderEnabled);
        }
        if (isColorEditEnabled) {
            LinearLayout.LayoutParams layoutParamsForColorEdit = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            colorEdit = (EditText) View.inflate(context, cc.brainbook.android.colorpicker.R.layout.color_edit, null);
            colorEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            colorEdit.setSingleLine();
            colorEdit.setVisibility(View.GONE);

            // limit number of characters to hexColors
            int maxLength = isAlphaSliderEnabled ? 9 : 7;
            colorEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

            pickerContainer.addView(colorEdit, layoutParamsForColorEdit);

            colorEdit.setText(Utils.getHexString(getStartColor(initialColor), isAlphaSliderEnabled));
            colorPickerView.setColorEdit(colorEdit);
        }
        if (isPreviewEnabled) {
            colorPreview = (LinearLayout) View.inflate(context, cc.brainbook.android.colorpicker.R.layout.color_preview, null);
            colorPreview.setVisibility(View.GONE);
            pickerContainer.addView(colorPreview);

            if (initialColor.length == 0) {
                ImageView colorImage = (ImageView) View.inflate(context, R.layout.color_selector, null);
                colorImage.setImageDrawable(new ColorDrawable(Color.WHITE));
            } else {
                for (int i = 0; i < initialColor.length && i < this.pickerCount; i++) {
                    if (initialColor[i] == null)
                        break;
                    LinearLayout colorLayout = (LinearLayout) View.inflate(context, R.layout.color_selector, null);
                    ImageView colorImage = (ImageView) colorLayout.findViewById(R.id.image_preview);
                    colorImage.setImageDrawable(new ColorDrawable(initialColor[i]));
                    colorPreview.addView(colorLayout);
                }
            }
            colorPreview.setVisibility(View.VISIBLE);
            colorPickerView.setColorPreview(colorPreview, getStartOffset(initialColor));
        }

    }

    private int getStartOffset(@NonNull Integer[] colors) {
        int start = 0;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == null) {
                return start;
            }
            start = (i + 1) / 2;
        }
        return start;
    }

    private int getStartColor(@NonNull Integer[] colors) {
        return colors[getStartOffset(colors)];
    }

    private static int getDimensionAsPx(@NonNull Context context, int rid) {
        return (int) (context.getResources().getDimension(rid) + .5f);
    }

    private void positiveButtonOnClick(DialogInterface dialog, @NonNull ColorPickerClickListener onClickListener) {
        int selectedColor = colorPickerView.getSelectedColor();
        Integer[] allColors = colorPickerView.getAllColors();
        onClickListener.onClick(dialog, selectedColor, allColors);
    }

}
