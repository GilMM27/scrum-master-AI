import { alpha, Avatar, Box, Divider, Drawer, List, ListItemButton, ListItemIcon, ListItemText, Stack, Toolbar, Typography } from "@mui/material"
import type { User } from "../../types/User.types";
import { NAVIGATION_BY_ROLE } from "./navigation.config";
import { HubOutlined } from "@mui/icons-material";
import { NavLink } from "react-router-dom";

interface AppSidebarProps {
  drawerWidth: number;
  mobileOpen: boolean;
  onCloseMobile: () => void;
  user: User;
}

const AppSideBarContent = ({ user, drawerWidth, onCloseMobile }: Omit<AppSidebarProps, 'mobileOpen'>) => {
  const navigationItems = NAVIGATION_BY_ROLE[user.role];

  return (
    <Box
      sx={{
        width: drawerWidth,
        height: '100%',
        bgcolor: 'background.paper',
        borderRight: '1px solid',
        borderColor: 'divider',
      }}
    >
      <Toolbar sx={{ px: 2, minHeight: '72px !important' }}>
        <Stack direction="row" spacing={1.5} sx={{ alignItems: 'center' }}>
          <Avatar alt="Agiflow logo"
            sx={{ width: 42,
              height: 42,
              bgcolor: alpha('#0016a6', 0.18),
              border: '1px solid',
              borderColor: alpha('#77ffc0', 0.25)
            }}
          >
            <HubOutlined sx={{ color: 'info.main'}} />
          </Avatar>
          <Box>
            <Typography variant="h6" sx={{ lineHeight: 1.1 }}>
              Agiflow
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Software Panel
            </Typography>
          </Box>
        </Stack>
      </Toolbar>

      <Divider />

      <List component="nav" sx={{ p: 1.5}}>
        {navigationItems.map((item) => {
          const Icon = item.icon;

          return (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              onClick={onCloseMobile}
              sx={{
                mb: 0.75,
                borderRadius: 3,
                color: 'text.secondary',
                '& .MuiListItemIcon-root': {
                  color: 'text.secondary',
                  minWidth: 36
                },
                '&.active': {
                  bgcolor: alpha('#77ffc0', 0.08),
                  color: 'info.main',
                  border: '1px solid',
                  borderColor: alpha('#77ffc0', 0.18)
                },
                '&.active .MuiListItemIcon-root': {
                  color: 'info.main'
                }
              }}
            >
              <ListItemIcon>
                <Icon />
              </ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          )
        })}
      </List>
    </Box>
  )
}

const AppSidebar = ({ drawerWidth, mobileOpen, onCloseMobile, user }: AppSidebarProps) => {
  return (
    <>
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={onCloseMobile}
        ModalProps={{ keepMounted: true }}
        sx={{ display: { xs: 'block', lg: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, bgcolor: 'background.paper' }
        }}
      >
        <AppSideBarContent user={user} drawerWidth={drawerWidth} onCloseMobile={onCloseMobile} />
      </Drawer>
      <Drawer
        variant="permanent"
        open
        sx={{ display: { xs: 'none', lg: 'block' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', 
            width: drawerWidth + 1, // +1 to compensate right border and avoid horizontal scrollbar
            bgcolor: 'background.paper', 
            borderRight: '1px solid', 
            borderColor: 'divider' 
          }
        }}
      >
        <AppSideBarContent user={user} drawerWidth={drawerWidth} onCloseMobile={() => {}} />
      </Drawer>
    </>
  )
}

export default AppSidebar