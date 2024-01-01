IF  EXISTS
(
	SELECT * FROM sys.foreign_keys
	WHERE object_id = OBJECT_ID(N'[dbo].[FK_RidgeBotStatistics]')
	AND parent_object_id = OBJECT_ID(N'[dbo].[RidgeBotStatistics]')
)
BEGIN
	ALTER TABLE [dbo].[RidgeBotStatistics]
	DROP CONSTRAINT [FK_RidgeBotStatistics]
END
GO

IF EXISTS
(
    SELECT * FROM sys.objects
    WHERE object_id = OBJECT_ID(N'[dbo].[RidgeBotStatistics]')
    AND type in (N'U')
)
BEGIN
  DROP TABLE [dbo].[RidgeBotStatistics]
END
GO

IF EXISTS
(
    SELECT * FROM sys.objects
    WHERE object_id = OBJECT_ID(N'[dbo].[RidgeBotTaskInfo]')
    AND type in (N'U')
)
BEGIN
  DROP TABLE [dbo].[RidgeBotTaskInfo]
END
GO