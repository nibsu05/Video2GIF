package model.Bean;

public class VideoRequest {
	Integer request_id, user_id;
	String original_video_name, video_path, gif_path, status, start_time, end_time;

	public Integer getRequest_id() {
		return request_id;
	}

	public void setRequest_id(Integer request_id) {
		this.request_id = request_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	
	public String getOriginal_video_name() {
		return original_video_name;
	}

	public void setOriginal_video_name(String original_video_name) {
		this.original_video_name = original_video_name;
	}

	public String getVideo_path() {
		return video_path;
	}

	public void setVideo_path(String video_path) {
		this.video_path = video_path;
	}
	
	public String getGif_path() {
		return gif_path;
	}

	public void setGif_path(String gif_path) {
		this.gif_path = gif_path;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
}
