import { Menu, type MenuProps } from "@mui/material";
import { styled } from "@mui/material/styles";

const StyledUserMenu = styled((props: MenuProps) => (
  <Menu
    elevation={0}
    anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
    transformOrigin={{ vertical: "top", horizontal: "right" }}
    {...props}
  />
))(({ theme }) => ({
  "& .MuiPaper-root": {
    marginTop: theme.spacing(1),
    minWidth: 260,
    borderRadius: 12,
    padding: theme.spacing(1),
    backgroundColor: theme.palette.background.paper,
  },
}));

export default StyledUserMenu;