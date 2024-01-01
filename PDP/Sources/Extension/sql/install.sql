--Table to store RidgeBot server task information
IF NOT EXISTS
(
    SELECT * FROM sys.objects
    WHERE object_id = OBJECT_ID(N'[dbo].[RidgeBotTaskInfo]')
    AND type in (N'U')
)
BEGIN
	CREATE TABLE [dbo].[RidgeBotTaskInfo] (
	[AutoId] [bigint]  NOT NULL PRIMARY KEY IDENTITY(1,1),
	[TaskId][nvarchar] (256) NOT NULL,
	[ServerName][nvarchar] (1024) NOT NULL,
	[ServerId][int] NOT NULL,
	[TaskJobTotal] [int],
	[TaskDetectType] [nvarchar] (256),
	[TaskJobCounts] [int],
	[TaskSummary] [nvarchar] (512),
  	[TaskName][nvarchar] (512) NOT NULL,	
	[TaskTargets][nvarchar] (max) NOT NULL,
	[TaskNodes][nvarchar] (max),
	[TaskStartTime][nvarchar] (256),
	[TaskProgress][nvarchar] (256) NOT NULL,
	[TaskStatus][nvarchar] (256) NOT NULL,
	[TaskCompleteTime][nvarchar] (256),
	[TaskType] [nvarchar] (512)
	--CONSTRAINT PK_RidgeBotTaskInfo PRIMARY KEY(TaskId,ServerName)
	 ) ON [PRIMARY]
END
GO

--Table to store RidgeBot server task statistics information
IF NOT EXISTS
(
    SELECT * FROM sys.objects
    WHERE object_id = OBJECT_ID(N'[dbo].[RidgeBotStatistics]')
    AND type in (N'U')
)
BEGIN
	CREATE TABLE [dbo].[RidgeBotStatistics] (
	[AutoId] [bigint]  NOT NULL PRIMARY KEY IDENTITY(1,1),
	[TaskId] [nvarchar] (256) NOT NULL,
	[ServerName] [nvarchar] (1024) NOT NULL,
	[ServerId][int] NOT NULL,
	[SecurityHttpsRatio] [int],
	[SecurityVulTransform] [int],
	[SecurityAttackSurfaceDensity] [int],
	[SecurityRiskTransform] [int],
	[SecuritySafetyIndex] [int] , -- This will be the health score for the task
	[SecurityAliveRatio] [int],
	[SecurityVulTypeCoverageRatio] [int],
	[TaskExploitCount] [int],
	[TaskDigCount] [int],
	[TaskScanCount] [int],
	[RiskCredentialsNum] [int],
	[RiskPenetrationNum] [int],
	[RiskInfiltratedAssetsNum] [int],
	[RiskExploitNum] [int],
	[RiskCodeNum] [int],
	[RiskNumber] [int],
	[RiskShellNum] [int],
	[RiskDatabaseNum] [int],
	[ChainSensitiveAttackAverage] [int],
	[ChainVulAttackAverage] [int],
	[VulNumber] [int],
	[VulInfo] [int],
	[VulTypeNumber] [int],
	[VulHigh] [int],
	[VulMedium] [int],
	[VulLow] [int],
	[AttackIpAliveNum] [int],
	[AttackIpAsstesNum] [int],
	[AttackIpUnaliveNum] [int],
	[AttackSiteAsstesNum] [int],
	[AttackSiteAliveNum] [int],
	[AttackSiteUnaliveNum] [int],
	[AttackAsstesNum] [int],
	[AttackSurfaceNum] [int]
	--CONSTRAINT FK_RidgeBotStatistics FOREIGN KEY (TaskId, ServerName)
    --REFERENCES RidgeBotTaskInfo (TaskId, ServerName)
    --ON DELETE CASCADE
    --ON UPDATE CASCADE
	)
END
GO
