import { Menu, type MenuProps } from "@mui/material";
import { styled } from "@mui/material/styles";

const StyledSettingsMenu = styled((props: MenuProps) => (
  <Menu
    elevation={0}
    anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
    transformOrigin={{ vertical: "top", horizontal: "right" }}
    {...props}
  />
))(({ theme }) => ({
  "& .MuiPaper-root": {
    marginTop: theme.spacing(1),
    minWidth: 220,
    borderRadius: 12,
    backgroundColor: theme.palette.background.paper,
  },
}));

export default StyledSettingsMenu;