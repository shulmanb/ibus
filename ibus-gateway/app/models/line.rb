class Line
  include ActiveModel::Serializers::JSON
  include ActiveModel::Serializers::Xml

  attr_accessor :id, :direction
  
  def attributes
    @attributes ||= {'id' => 'nil','direction'=>'nil'}
  end
  
 end