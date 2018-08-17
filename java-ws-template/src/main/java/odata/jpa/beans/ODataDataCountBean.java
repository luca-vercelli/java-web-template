package odata.jpa.beans;

import java.util.List;

public class ODataDataCountBean extends ODataDataBean {

	private Long count;

	public ODataDataCountBean() {
	}

	public ODataDataCountBean(List<?> data, Long count) {
		super(data);
		this.count = count;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
