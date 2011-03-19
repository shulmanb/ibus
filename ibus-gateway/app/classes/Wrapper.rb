require 'java'
require 'lib/jsi-1.0b6.jar'
require 'lib/log4j-1.2.16.jar'
require 'lib/sil-0.44.2b.jar'
require 'lib/trove-2.0.2.jar'
require 'lib/aws-java-sdk-1.1.0.jar'
require 'lib/commons-codec-1.3.jar'
require 'lib/commons-httpclient-3.1.jar'
require 'lib/commons-logging-1.1.1.jar'
require 'lib/commons-pool-1.5.5.jar'
require 'lib/gson-1.5.jar'
require 'lib/jackson-core-asl-1.6.2.jar'
require 'lib/jackson-mapper-asl-1.6.2.jar'
require 'lib/jedis-1.5.2.jar'
require 'lib/stax-api-1.0.1.jar'
require 'lib/tracer-0.0.4-SNAPSHOT.jar'
require 'lib/common-0.0.4-SNAPSHOT.jar'
require 'lib/mapbuilder-0.0.4-SNAPSHOT.jar'
require 'lib/navigation-0.0.4-SNAPSHOT.jar'
require 'lib/guava-r07.jar'
require 'lib/aopalliance-1.0.jar'
require 'lib/guice-2.0.jar'

import com.ibus.map.Point
import com.ibus.map.TimedPoint
import com.ibus.map.Stop
import com.ibus.map.StopsRoute
import com.ibus.map.LineSegment
import com.ibus.map.AreaDetails
import com.ibus.map.LineDetails
import com.ibus.map.Lines
import com.ibus.map.Line
import com.ibus.map.StopDetails
import com.ibus.tracer.Status

import com.ibus.mapbuilder.Mapbuilder
import com.ibus.navigation.map.MapQuery
import com.ibus.navigation.map.Navigator
import com.ibus.tracer.Tracer
import com.ibus.tracer.BusPositionTracer
import com.ibus.tracer.SessionManager
import com.ibus.tracer.module.TracerModule
import com.ibus.navigation.module.NavigationModule
import com.ibus.mapbuilder.module.MapbuilderModule


class Line
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'id'=>nil,'name'=>nil}
  end
end

class Lines
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'lines'=>[]}
  end
end


class Status
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'status'=>nil}
  end
end

class LineDetails
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'id'=>nil,'name'=>nil,'points'=>[]}
  end
end

class AreaDetails
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'lines'=>[],'stops'=>[]}
  end
end

class Stop
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'longitude'=>nil,'latitude'=>nil,'desc'=>nil}
  end
end

class StopDetails
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'longitude'=>nil,'latitude'=>nil,'desc'=>nil,'submap'=>nil,'lines'=>[]}
  end
end

class Point
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'longitude'=>nil,'latitude'=>nil}
  end
end

class TimedPoint
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'longitude'=>nil,'latitude'=>nil,'time'=>nil}
  end
end

class LineSegment
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'points'=>[],'duration'=>nil,'lineId'=>nil,'line'=>nil,'start'=>nil,'end'=>nil}
  end
end

class StopsRoute
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml
  def attributes
    @attributes ||= {'routeId'=>nil,'route'=>[],'weight'=>nil,'dest'=>nil}
  end
end


class Wrapper

  def initialize()
    TracerModule.initialize "localhost",6379
    @tracer = TracerModule.getTracer 
    @sessionMgr = TracerModule.getSessionManager
    @bus_tracer = TracerModule.getBusPositionTracer
    MapbuilderModule.initialize "localhost",6379,"AKIAIJPN5YYDFNRZHUSQ","0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB"
    @builder = MapbuilderModule.getMapbuilder
    
    NavigationModule.initialize "AKIAIJPN5YYDFNRZHUSQ","0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB"
    @query = NavigationModule.getMapQuery
    @navigator = NavigationModule.getNavigator  
  end
 
#  @@builder = Mapbuilder.new("localhost",6379,"AKIAIJPN5YYDFNRZHUSQ","0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB")
#  @@query = MapQuery.new("AKIAIJPN5YYDFNRZHUSQ","0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB")
#  @@navigator = Navigator.new("AKIAIJPN5YYDFNRZHUSQ","0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB")

  
     
  def initiateRouteRecording(ts,lat,lon,line,submap )
    @builder.initiateRouteRecording ts.to_i, Point.new(lon.to_f,lat.to_f), line, submap
  end

  def addPoint(sessionId,ts,lat,lon)
    @builder.addPoint sessionId, ts.to_i, Point.new(lon.to_f,lat.to_f)
  end

  def addStation(sessionId, ts, lat,lon,desc="")
    @builder.addStation sessionId, ts.to_i, Point.new(lon.to_f,lat.to_f)
  end

  def finishRouteRecording(sessionId, ts, lat, lon,desc="")
    @builder.finishRouteRecording sessionId, ts.to_i, Point.new(lon.to_f,lat.to_f)
  end

  def getStopsInAreaByCorners(submap,leftlat, leftlon,rightlat, rightlon)
    @query.getStopsInArea submap,Point.new(leftlon.to_f,leftlat.to_f),Point.new(rightlon.to_f,rightlat.to_f)
  end

  def getStopsInAreaByCenter(submap,centerlat, centerlon,latoffset,lonoffset)
    stops = @query.getStopsInArea submap,Point.new(centerlon.to_f,centerlat.to_f),latoffset.to_i,lonoffset.to_i
  end

  def getLineDetails(lineId,level)
    @query.getLineDetails(lineId,level)
  end

  def getLineDetails(lineId)
    @query.getLineDetails(lineId,10)
  end

  def getLineStations(lineId)
    @query.getLineStations(lineId)
  end

  def getLinesForStation(stationid)
    strarr = @query.getLinesForStation(stationid)
    lines = Array.new
    strarr.each{|s|
      l = Line.new
      l.id = s
      lines << l
    }
    return lines
  end

  def getStation(stationId)
    @query.getStation(stationId)
  end

  def getLineDetailsInAreaByCorners(lineId,leftlat,leftlon,rightlat,rightlon,level)
    @query.getLineDetailsInArea lineId,Point.new(leftlon.to_f,leftlat.to_f),Point.new(rightlon.to_f,rightlat.to_f),level.to_i
  end

  def getLineDetailsInAreaByCenter(lineId,centerlat,centerlon,latoffset,lonoffset,level)
    @query.getLineDetailsInArea lineId,Point.new(centerlon.to_f,centerlat.to_f),latoffset.to_i,lonoffset.to_i,level.to_i
  end

  def getLineStationsInAreaByCorners(lineId,leftlat,leftlon,rightlat,rightlon)
    stops = @query.getLineStationsInArea lineId,Point.new(leftlon.to_f,leftlat.to_f),Point.new(rightlon.to_f,rightlat.to_f)
  end

  def getLineStationsInAreaByCenter(lineId,centerlat, centerlon,latoffset,lonoffset)
    @query.getLineStationsInArea lineId,Point.new(centerlon.to_f,centerlat.to_f),latoffset.to_i,lonoffset.to_i
  end

  def getAreaDetailsByCorners(leftlat,leftlon,rightlat,rightlon)
    stops = @query.getAreaDetails Point.new(leftlon.to_f,leftlat.to_f),Point.new(rightlon.to_f,rightlat.to_f)
  end

  def getAreaDetailsByCenter(centerlat, centerlon,latoffset,lonoffset)
    @query.getAreaDetails Point.new(centerlon.to_f,centerlat.to_f),latoffset.to_i,lonoffset.to_i
  end

  def getLinesInSubmap(submap)
    @query.getLinesInSubmap submap
  end

  def deleteLine(lineId)
    @query.deleteLine lineId
  end

  
  def navigate(origlat,origlon, destlat, destlon,submap)
    stops_route = @navigator.navigate Point.new(origlon.to_f,origlat.to_f),Point.new(destlon.to_f,destlat.to_f),submap
    if stops_route != nil
      @tracer.storeTemporaryRoute stops_route
    end 
    return stops_route
  end
  
  def createAnonimousSession(clientid)
    @sessionMgr.createSession clientid
  end
  
  def createSession(user, passwd)
      @sessionMgr.createSession user, passwd
  end
  
  def validateSession(session)
    @sessionMgr.validateSession session
  end
  
  def storeLocation(session, lon, lat, ts)
    @tracer.storeLocation session, TimedPoint.new(lon.to_f,lat.to_f,ts.to_i)
  end
  
  def storeLocationOnRoute(session, lon, lat, ts)
      @tracer.storeLocationOnRoute session, TimedPoint.new(lon.to_f,lat.to_f,ts.to_i)
  end

  def checkInToRoute(session, routeId)
    @tracer.checkInToRoute session, routeId
  end
  
  def getLocation(session)
    @tracer.getLocation session
  end  

  def getCheckInStatus(session)
    @tracer.getCheckInStatus(session)
  end
     
  def tracBus(lineid, lon, lat)
    @bus_tracer.tracPosition(lineid,Point.new(lon.to_f,lat.to_f))
  end
  
  def queryBusLocations(lineid)
    @bus_tracer.getLineBuses(lineid)
  end
  
  def lineOffset(lineid,time)
    line = @query.getLineDetails(lineid,10);
    total_offset = 0
    offset = 0
    for point in line
      if (offset+point.time) > time.to_i*1000
        return point
      else
        if (offset != 0) and (point.time == 0)
          total_offset+=offset
        elsif
          offset = point.time
        end
      end
    end
  end
  
end
