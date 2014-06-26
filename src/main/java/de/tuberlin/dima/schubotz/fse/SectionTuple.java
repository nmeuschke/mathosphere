package de.tuberlin.dima.schubotz.fse;

import eu.stratosphere.api.java.tuple.Tuple3;

public class SectionTuple extends Tuple3<SectionNameTuple, explicitDataSet<ResultTuple>, explicitDataSet<KeyWordTuple>> {
	public Object getNamedField (fields f) {
		return getField( f.ordinal() );
	}

	public void setNamedField (fields f, Object value) {
		setField( value, f.ordinal() );
	}

	public enum fields {
		name, formulae, keywords
	}
}
