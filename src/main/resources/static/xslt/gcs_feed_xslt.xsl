<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="https://gcs.retail.schawk.com/schema/GcsFeedSchema" xmlns:foo="http://www.foo.org" version="2.0">
   <xsl:output omit-xml-declaration="yes" indent="yes"/>
   <xsl:param name="current-date" />
   <xsl:param name="user-name" />
   <xsl:param name="password" />
   <xsl:template match="/">
      <GcsFeed xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="https://gcs.retail.schawk.com/schema/GcsFeedSchema https://gcs.retail.schawk.com/schema/GcsFeedSchema1.0.xsd">
         <FeedHeader>
            <FeedSecurityInfo>
               <gcsLogin><xsl:value-of select="$user-name"/></gcsLogin>
               <encryptedPassword><xsl:value-of select="$password"/></encryptedPassword>
               <mutualToken>ForFutureUse</mutualToken>
               <assumedUser />
            </FeedSecurityInfo>
            <FeedClientInfo>
               <ClientCode>NIKE</ClientCode>
               <ClientName>NIKE</ClientName>
               <StyleGuide />
            </FeedClientInfo>
            <SourceFeedInfo>
               <SourceFeedFileName>May26_normal_feed.xml</SourceFeedFileName>
               <SourceFeedFileType>XML</SourceFeedFileType>
               <SourceFeedReceivedDateTime><xsl:value-of select="$current-date"/></SourceFeedReceivedDateTime>
               <SourceFeedProcessedDateTime><xsl:value-of select="$current-date"/></SourceFeedProcessedDateTime>
            </SourceFeedInfo>
         </FeedHeader>
         <WorkRequests>
            <xsl:for-each select="/products/PRODUCT">
               <FeedWorkRequest>
                  <xsl:variable name="requestName">
                     <xsl:apply-templates select="stylenumber" />
                     <xsl:apply-templates select="colornumber" />
                  </xsl:variable>
                  <xsl:variable name="styleDetails">
                     <xsl:apply-templates select="styledetail" />
                  </xsl:variable>
                  <xsl:variable name="sizeDetails">
                     <xsl:apply-templates select="sizes" />
                  </xsl:variable>
		  <xsl:variable name="genderDetails">
                     <xsl:apply-templates select="attributes/attribute[@type='gender']/code" />
                  </xsl:variable>
                  <WorkRequestName>
                     <xsl:value-of select="$requestName" />
                  </WorkRequestName>
                  <Department />
                  <RequestDetails>
                     <BusinessUnits>
                        <xsl:for-each select="requestors/requestor">
                           <BusinessUnit>
                              <BuName>
                                 <xsl:value-of select="@name" />
                              </BuName>
                              <RequestStatus>
                                 <xsl:choose>
                                    <xsl:when test="status='SOON' or status ='INACTIVE' or status ='HOLD'">
                                       <xsl:text>REQUESTED</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                       <xsl:text>CANCELLED</xsl:text>
                                    </xsl:otherwise>
                                 </xsl:choose>
                              </RequestStatus>
                              <CompleteFeedAsText>
                                 N/A
                              </CompleteFeedAsText>
                              <IndicatedDueDate />
                              <IndicatedPriority>
                                 <xsl:choose>
                                    <xsl:when test="inventory='true'">
                                       <xsl:text>High</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                       <xsl:text>Low</xsl:text>
                                    </xsl:otherwise>
                                 </xsl:choose>
                              </IndicatedPriority>
                              <BuSpecificAdditionalAttributes>
                                 <Attribute>
                                    <AttributeName>STARTDATE</AttributeName>
                                    <AttributeDataType>DATE</AttributeDataType>
                                    <AttributeValue>
                                       <xsl:value-of select="startdate" />
                                    </AttributeValue>
                                 </Attribute>
                                 <Attribute>
                                    <AttributeName>HARDLAUNCH</AttributeName>
                                    <AttributeDataType>TEXT</AttributeDataType>
                                    <AttributeValue>
                                       <xsl:value-of select="parent::*/parent::*/attributes/attribute[@type='hardlaunch']/code" />
                                    </AttributeValue>
                                 </Attribute>
                              </BuSpecificAdditionalAttributes>
                           </BusinessUnit>
                        </xsl:for-each>
                     </BusinessUnits>
                     <BillToBusinessUnit />
                     <RequestSpecificAdditionalAttributes>
                        <Attribute>
                           <AttributeName>Inventory</AttributeName>
                           <AttributeDataType>BOOLEAN</AttributeDataType>
                           <AttributeValue>
                              <xsl:value-of select="requestors/requestor/inventory" />
                           </AttributeValue>
                        </Attribute>
			<Attribute>
                           <AttributeName>KeyMarketing</AttributeName>
                           <AttributeDataType>BOOLEAN</AttributeDataType>
                           <AttributeValue>
                              <xsl:value-of select="attributes/attribute[@type='key']/code" />
                           </AttributeValue>
                        </Attribute>
                     </RequestSpecificAdditionalAttributes>
                  </RequestDetails>
                  <MerchDetails>
                     <FeedMerchInfo>
                        <ItemNumber>
                           <xsl:value-of select="$requestName" />
                        </ItemNumber>
                        <ItemName>
                           <xsl:value-of select="styledetail/style/stylename" />
                        </ItemName>
                        <ItemFamily>
                           <xsl:value-of select="stylenumber" />
                        </ItemFamily>
                        <ItemType>
                           <xsl:value-of select="niketype" />
                        </ItemType>
                        <ItemDescription />
                        <ItemSize>
                           <xsl:value-of select="$sizeDetails" />
                        </ItemSize>
                        <ItemColorCode>
                           <xsl:value-of select="colornumber" />
                        </ItemColorCode>
                        <ItemColorDescription>
                           <xsl:value-of select="styledetail/style/colordescription" />
                        </ItemColorDescription>
                        <Gender>
				<xsl:value-of select="$genderDetails" />
                        </Gender>
                        <ItemSide />
                        <ItemSerialNumber />
                        <ItemUPC />
                        <ItemSKUs>
                           <xsl:for-each select="sizes/size/upc">
                              <SKUNumber>
                                 <xsl:value-of select="." />
                              </SKUNumber>
                           </xsl:for-each>
                        </ItemSKUs>
                        <AssemblyRequired>false</AssemblyRequired>
                        <ItemVendor />
                        <ItemQuantityForShot>0</ItemQuantityForShot>
                        <Comments />
                        <MerchSpecificAdditionalAttributes>
                           <Attribute>
                              <AttributeName>Classification</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="attributes/attribute[@type='classid']/code" />
                              </AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>SportPrimary</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue><xsl:value-of select="attributes/attribute[@type='primarysport']/code" /></AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>Brand</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue><xsl:value-of select="attributes/attribute[@type='primarysport']/code" /></AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>CopyApproved</AttributeName>
                              <AttributeDataType>BOOLEAN</AttributeDataType>
                              <AttributeValue />
                           </Attribute>
                           <Attribute>
                              <AttributeName>ImageApproved</AttributeName>
                              <AttributeDataType>BOOLEAN</AttributeDataType>
                              <AttributeValue />
                           </Attribute>
                           <Attribute>
                              <AttributeName>NFLTeamName</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="attributes/attribute[@type='nflteamname']/code" />
                              </AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>NFLAthleteName</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="attributes/attribute[@type='nflathleteName']/code" />
                              </AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>Athlete</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="attributes/attribute[@type='athlete']/code" />
                              </AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>hexvalue</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="attributes/attribute[@type='hexvalue']/code" />
                              </AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>SwatchColorDesc</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="styledetail/style/colordescription" />
                              </AttributeValue>
                           </Attribute>
                           <Attribute>
                              <AttributeName>Fangear</AttributeName>
                              <AttributeDataType>TEXT</AttributeDataType>
                              <AttributeValue>
                                 <xsl:value-of select="attributes/attribute[@type='Fan Gear']/code" />
                              </AttributeValue>
                           </Attribute>
                        </MerchSpecificAdditionalAttributes>
                     </FeedMerchInfo>
                  </MerchDetails>
		  <Medias />
               </FeedWorkRequest>
            </xsl:for-each>
         </WorkRequests>
      </GcsFeed>
   </xsl:template>
   <xsl:template match="requestor/startdate">
      <Attribute>
         <AttributeName>
            <xsl:value-of select="name(.)" />
         </AttributeName>
         <AttributeDataType>DATE</AttributeDataType>
         <AttributeValue>
            <xsl:value-of select="." />
         </AttributeValue>
      </Attribute>
   </xsl:template>
   <xsl:template match="requestor/status">
      <Attribute>
         <AttributeName>
            <xsl:value-of select="name(.)" />
         </AttributeName>
         <AttributeDataType>TEXT</AttributeDataType>
         <AttributeValue>
            <xsl:value-of select="." />
         </AttributeValue>
      </Attribute>
   </xsl:template>
   <xsl:template match="//PRODUCT/stylenumber">
      <xsl:apply-templates />
      <xsl:text>_</xsl:text>
   </xsl:template>
   <xsl:template match="//PRODUCT/colornumber">
      <xsl:apply-templates />
   </xsl:template>
   <xsl:template match="styledetail">
      <xsl:apply-templates select="style/child::*" />
   </xsl:template>
   <xsl:template match="style/child::*">
      <xsl:apply-templates />
      <xsl:text>,</xsl:text>
   </xsl:template>
   <xsl:template match="sizes">
      <xsl:apply-templates select="size/size" />
   </xsl:template>
   <xsl:template match="sizes/size/size">
      <xsl:apply-templates />
      <xsl:text>,</xsl:text>
   </xsl:template>
    <xsl:template match="attributes/attribute[@type='gender']">
      <xsl:apply-templates select="code" />
   </xsl:template>
   <xsl:template match="code">
      <xsl:apply-templates />
      <xsl:text>,</xsl:text>
   </xsl:template>
   <xsl:template match="*" />
</xsl:stylesheet>