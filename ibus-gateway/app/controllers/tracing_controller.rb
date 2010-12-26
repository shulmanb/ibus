class TracingController < ApplicationController
  
  def update_location
    session = params[:session]
    lat = params[:data][:lat]
    lon = params[:data][:lon]
    ts = params[:data][:ts]
    @@wrapper.storeLocation session, lat, lon, ts
    format_empty(:ok)         
  end
  
  def update_location_on_route
    session = params[:session]
    lat = params[:data][:lat]
    lon = params[:data][:lon]
    ts = params[:data][:ts]
    @status = @@wrapper.storeLocationOnRoute session, lat, lon, ts
    format @status        
  end

  def get_location
    session = params[:session]
    @location = @@wrapper.getLocation session
    format @location
  end
  
  def checkin_to_route
    session = params[:session]
    route = params[:data][:route]
    @@wrapper.checkInToRoute session, route
    format_empty(:ok)  
  end  

  def get_checkin_status
    session = params[:session]
    @checkin  = Checkin.new
    @checkin.route = @@wrapper.getCheckInStatus session
    format @checkin
  end
  
end
