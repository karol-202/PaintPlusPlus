package pl.karol202.paintplus;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

public class ColorsSelect extends Fragment implements View.OnClickListener, OnColorSelectedListener
{
	public interface OnColorSelectedListener
	{
		void onColorSelected(ColorsSet colors);
	}

	private ColorsSet colors;
	private OnColorSelectedListener listener;

	private View colorFirst;
	private View colorSecond;
	private ImageButton buttonSwap;
	private int target;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		try
		{
			listener = (OnColorSelectedListener) getActivity();
		}
		catch(ClassCastException e)
		{
			throw new ClassCastException(getActivity().toString() + " must implement OnColorSelectedListener.");
		}

		View view = inflater.inflate(R.layout.colors, container, false);

		colorFirst = view.findViewById(R.id.view_color_first);
		colorFirst.setOnClickListener(this);

		colorSecond = view.findViewById(R.id.view_color_second);
		colorSecond.setOnClickListener(this);

		buttonSwap = (ImageButton) view.findViewById(R.id.button_colors_swap);
		buttonSwap.setOnClickListener(this);

		return view;
	}

	public void setColors(ColorsSet colors)
	{
		this.colors = colors;
		updateColors();
	}

	@Override
	public void onClick(View v)
	{
		if(v == buttonSwap) colors.revert();
		else if(v == colorFirst) pickColor(0);
		else if(v == colorSecond) pickColor(1);
		listener.onColorSelected(colors);
		updateColors();
	}

	private void updateColors()
	{
		colorFirst.setBackgroundColor(colors.getFirstColor());
		colorSecond.setBackgroundColor(colors.getSecondColor());
	}

	private void pickColor(int target)
	{
		this.target = target;
		int color = target == 0 ? colors.getFirstColor() : colors.getSecondColor();
		new ChromaDialog.Builder().colorMode(ColorMode.RGB)
								  .initialColor(color)
								  .indicatorMode(IndicatorMode.DECIMAL)
								  .onColorSelected(this)
								  .create()
								  .show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "ColorPicker");
	}

	@Override
	public void onColorSelected(@ColorInt int color)
	{
		if(target == 0) colors.setFirstColor(color);
		else if(target == 1) colors.setSecondColor(color);
		listener.onColorSelected(colors);
		updateColors();
	}
}
