package com.questhelper.questinfo;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.config.ConfigManager;

public class HelperConfig
{
	@Getter
	String name;
	@Getter
	String key;
	@Getter
	Enum[] enums;
	@Getter @Setter
	boolean allowMultiple = false;
	@Getter @Setter
	boolean customRender = false;
	public HelperConfig(String name, String key, Enum[] enums)
	{
		this.name = name;
		this.key = key;
		this.enums = enums;
	}

	public String[] getValues()
	{
		List<String> s = new ArrayList<>();
		for (Enum value : enums)
		{
			s.add(value.name());
		}
		return s.toArray(s.toArray(new String[0]));
	}
	
	@Nullable
	public Component render(ConfigManager configManager)
	{
		return null;
	}
}
