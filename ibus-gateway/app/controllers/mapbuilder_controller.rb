#provides the map building API
#
#Recording API:
#
#1) create recording session (with first station)
#
#POST /lines
#REQUEST: {data:{lat:xxxx,long:xxx,desc:xxx,lineid:"route name",ts:xxx,submap:"il_yoqneam"}
#RESPONSE: "id"
#
#2) add point to route
#POST /lines/:id/point
#REQUEST: {lat:xxxx,long:xxx, ts:xxx}
#
#3) add station to route
#POST /lines/:id/station
#REQUEST: {lat:xxxx,long:xxx, ts:xxx,desc:xxx}
#
#4) finish recording station
#
#POST /route/:id/laststation
#REQUEST: {lat:xxxx,long:xxx, desc:xxx}

class MapbuilderController < ApplicationController
  
  def initiate
    lat = params[:data][:lat].gsub("_",".")
    long = params[:data][:long].gsub("_",".")
    line = params[:data][:lineid]
    desc = params[:data][:desc]
    ts = params[:data][:ts]
    submap = params[:data][:submap]
    @s = Session.new
    @s.id=@@wrapper.initiateRouteRecording ts, lat, long, line, submap
    format @s
  end

  def addpoint
    lat = params[:data][:lat].gsub("_",".")
    long = params[:data][:long].gsub("_",".")
    ts = params[:data][:ts]
    id = params[:id]
    @@wrapper.addPoint(id,ts,lat,long)  
    format_empty :ok
  end

  def addstation
    lat = params[:data][:lat].gsub("_",".")
    long = params[:data][:long].gsub("_",".")
    desc = params[:data][:desc]
    ts = params[:data][:ts]
    id = params[:id]
    @@wrapper.addStation(id,ts,lat,long)  
    format_empty :ok
  end

  def complete
    lat = params[:data][:lat].gsub("_",".")
    long = params[:data][:long].gsub("_",".")
    desc = params[:data][:desc]
    ts = params[:data][:ts]
    id = params[:id]
    @@wrapper.finishRouteRecording(id,ts,lat,long)
    format_empty :ok
  end
end
