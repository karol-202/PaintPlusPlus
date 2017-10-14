package pl.karol202.paintplus.history;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

class HistoryActionViewHolder extends HistoryViewHolder
{
	static final int PREVIEW_SIZE_DP = 60;
	
	private OnHistoryUpdateListener listener;
	private ImageView imagePreview;
	private TextView textName;
	private ImageButton buttonUndo;
	private ImageButton buttonRedo;
	
	HistoryActionViewHolder(View view, OnHistoryUpdateListener listener)
	{
		super(view);
		this.listener = listener;
		
		imagePreview = view.findViewById(R.id.image_history_action_preview);
		textName = view.findViewById(R.id.text_history_action);
		buttonUndo = view.findViewById(R.id.button_history_action_undo);
		buttonRedo = view.findViewById(R.id.button_history_action_redo);
	}
	
	@Override
	void bind(final History history, Action action)
	{
		imagePreview.setImageBitmap(action.getActionPreview());
		textName.setText(action.getActionName());
		
		buttonUndo.setVisibility(history.canActionBeUndoneNow(action) ? View.VISIBLE : View.INVISIBLE);
		buttonUndo.setEnabled(action.isActionDone());
		buttonUndo.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				history.undo();
				listener.onHistoryUpdated();
			}
		});
		
		buttonRedo.setVisibility(history.canActionBeRedoneNow(action) ? View.VISIBLE : View.INVISIBLE);
		buttonRedo.setEnabled(!action.isActionDone());
		buttonRedo.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				history.redo();
				listener.onHistoryUpdated();
			}
		});
	}
}