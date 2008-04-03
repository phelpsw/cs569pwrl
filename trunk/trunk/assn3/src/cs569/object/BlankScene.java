package cs569.object;

public class BlankScene implements ParameterizedObjectMaker {

	@Override
	public HierarchicalObject make(Object... inputs) {
		Scene out = new Scene();

		return out;
	}

}
