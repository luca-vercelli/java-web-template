package odata.jpa.beans;

import java.util.List;

public class ODataDataBean {

	private List<?> data;

	public ODataDataBean() {
	}

	public ODataDataBean(List<?> data) {
		this.data = data;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}
}
