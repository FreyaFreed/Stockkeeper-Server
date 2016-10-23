package stockkeeper.data;

import java.io.Serializable;

public class Stack implements Serializable{
	public String name;
	public int size;
	public String serializedStack;
	public Stack(String name_, int size_, String serializedStack_)
	{
		name = name_;
		size = size_;
		serializedStack = serializedStack_;

	}

}
