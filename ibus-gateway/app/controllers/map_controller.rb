#curl -X POST http://localhost:3000/lines/1/point.json -H "Content-Type:application/json" -d "{'long':'1','lat':'1','ts':'222'}"

#Map Query API:
#
#1) getStationsInRect(center point, x offset in meters, y offset in meters)
#
#GET /stations/by_center/:lat/:long/:latoffst/:longoffst
#GET /stations/by_center?lat=xxx&?long=xxx&latoffst=xxx&longoffst=xxx
#RESPONSE: {[station:{lat:xxx,long:xxx,desc:xxx, id:xxx},station:{lat:xxx,long:xxx,desc:xxx, id:xxx}]}
#2) getStationsInRect(upper left corner, lower right corner)
#
#GET /stations/by_corners/:lat1/:long1/:lat2/:long2
#GET /stations/by_corners?lat1=xxx&?long1=xxx&lat2=xxx&long2t=xxx
#RESPONSE: {[station:{lat:xxx,long:xxx,desc:xxx, id:xxx},station:{lat:xxx,long:xxx,desc:xxx, id:xxx}]}
#
#3) getLaneDetails
#
#GET /lines/:id/:level
#GET /lines/:id&level=xxx
#levels: 1-10, when 1 is most detailed
#RESPONSE: {[point:{lat:xxx,long:xxx},point:{lat:xxx,long:xxx}]}
#
#4) getlinestations
#GET /lines/:id/stations
#RESPONSE: {[station:{lat:xxx,long:xxx,desc:xxx, id:xxx},station:{lat:xxx,long:xxx,desc:xxx, id:xxx}]}
#
#5) getlinesForStation
#GET /stations/:id/lines
#RESPONSE: [line:{id:5,dir:"last station id"}]
#
#6)getStationById
#GET /stations/:id
#RESPONSE: detailedstation:{lat:xxx,long:xxx,desc:xxx, id:xxx, lines:{[{id:5,dir:"last station id"}]}}
#
#
#7) getLaneDetailsInRect
#GET /lines/:id/by_center/:lat/:long/:latoffst/:longoffst/:level
#RESPONSE: {[point:{lat:xxx,long:xxx},point:{lat:xxx,long:xxx}]}
#
#8) getLaneDetailsInRect
#GET /lines/:id/by_corners/:lat1/:long1/:lat2/:long2
#RESPONSE: {[point:{lat:xxx,long:xxx},point:{lat:xxx,long:xxx}]}
#
#9) getlinestationsInRect
#GET /lines/:id/stations/by_center/:lat/:long/:latoffst/:longoffst
#RESPONSE: {[station:{lat:xxx,long:xxx,desc:xxx, id:xxx},station:{lat:xxx,long:xxx,desc:xxx, id:xxx}]}
#
#10) getlinestationsInRect
#GET /lines/:id/stations/by_corners/:lat1/:long1/:lat2/:long2
#RESPONSE: {[station:{lat:xxx,long:xxx,desc:xxx, id:xxx},station:{lat:xxx,long:xxx,desc:xxx, id:xxx}]}
class MapController < ApplicationController
  
  
  def line_stations_in_area_by_center
    longoffset = params[:longoffst]
    latoffset = params[:latoffst]
    long = params[:long].gsub("_",".")
    lat = params[:lat].gsub("_",".")
    lineId = params[:id]
    @stations = @@wrapper.getLineStationsInAreaByCenter(lineId,lat,long,latoffset,longoffset)
    format @stations  
  end
  
  def line_stations_in_area_by_corners
    long1 = params[:long1].gsub("_",".")
    lat1 = params[:lat1].gsub("_",".")
    long2 = params[:long2].gsub("_",".")
    lat2 = params[:lat2].gsub("_",".")
    lineId = params[:id]
    @stations = @@wrapper.getLineStationsInAreaByCorners(lineId,lat1,long1,lat2,long2)
    format @stations  
  end

  def line_details_in_area_by_center
    longoffset = params[:longoffst]
    latoffset = params[:latoffst]
    long = params[:long].gsub("_",".")
    lat = params[:lat].gsub("_",".")
    level = params[:level]
    lineId = params[:id]
    @points = @@wrapper.getLineDetailsInAreaByCenter(lineId,lat,long,latoffset,longoffset,level)
    format @points  
  end
  
  def line_details_in_area_by_corners
    long1 = params[:long1].gsub("_",".")
    lat1 = params[:lat1].gsub("_",".")
    long2 = params[:long2].gsub("_",".")
    lat2 = params[:lat2].gsub("_",".")
    lineId = params[:id]
    level = params[:level]
    @points = @@wrapper.getLineDetailsInAreaByCorners(lineId,lat1,long1,lat2,long2,level)
    format @points  
  end

  def station_details
    stationid = params[:id].gsub("_",".").gsub(":","_")
    @details = @@wrapper.getStation stationid
    format @details  
  end
   
  def line_details
    level = params[:level]
    lineid = params[:id]
    @points = @@wrapper.getLineDetails(lineid,level)
    format @points    
  end
  
  def line_stations
    lineid = params[:id]
    @stations = @@wrapper.getLineStations(lineid)
    
    format @stations
  end
  
  def lines_for_station
    stationid = params[:id].gsub("_",".").gsub(":","_")
    @lines = @@wrapper.getLinesForStation(stationid)
    format @lines
  end

  def stations_by_corners
    long2 = params[:long2].gsub("_",".")
    lat2 = params[:lat2].gsub("_",".")
    long1 = params[:long1].gsub("_",".")
    lat1 = params[:lat1].gsub("_",".")
    @stations = @@wrapper.getStopsInAreaByCorners('il_yoqneam',lat1,long1,lat2,long2)
    format @stations
  end

  def stations_by_center
    longoffset = params[:longoffst]
    latoffset = params[:latoffst]
    long = params[:long].gsub("_",".")
    lat = params[:lat].gsub("_",".")
    print "OFFSETS #{latoffset} #{longoffset}"

      @stations = @@wrapper.getStopsInAreaByCenter('il_yoqneam',lat,long,latoffset,longoffset)
    format @stations
  end
  def area_details_by_center
    longoffset = params[:longoffst]
    latoffset = params[:latoffst]
    long = params[:long].gsub("_",".")
    lat = params[:lat].gsub("_",".")
    @details = @@wrapper.getAreaDetailsByCenter(lat,long,latoffset,longoffset)
    format @details  
  end
  
  def area_details_by_corners
    long1 = params[:long1].gsub("_",".")
    lat1 = params[:lat1].gsub("_",".")
    long2 = params[:long2].gsub("_",".")
    lat2 = params[:lat2].gsub("_",".")
    @details = @@wrapper.getAreaDetailsByCorners(lat1,long1,lat2,long2)
    format @details  
  end

  def lines_in_submap
    submap = params[:submap]
    @lines = @@wraper.getLinesInSubmap submap
    format @lines  
  end
  
  def delete_line
    lineid = params[:id]
    @@wraper.deleteLine lineid
    format_empty :ok  
  end
  
end
