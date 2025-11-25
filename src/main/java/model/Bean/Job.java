package model.Bean;

public class Job {
	Integer job_id, request_id;
	String status;

	public Integer getJob_id() {
		return job_id;
	}

	public void setJob_id(Integer job_id) {
		this.job_id = job_id;
	}

	public Integer getRequest_id() {
		return request_id;
	}

	public void setRequest_id(Integer request_id) {
		this.request_id = request_id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}
