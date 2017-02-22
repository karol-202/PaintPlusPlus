package pl.karol202.paintplus.color.manipulators.params;

public abstract class ColorsManipulatorParams
{
	private ManipulatorSelection selection;
	
	public ColorsManipulatorParams(ManipulatorSelection selection)
	{
		this.selection = selection;
	}
	
	public ManipulatorSelection getSelection()
	{
		return selection;
	}
	
	public void setSelection(ManipulatorSelection selection)
	{
		this.selection = selection;
	}
}